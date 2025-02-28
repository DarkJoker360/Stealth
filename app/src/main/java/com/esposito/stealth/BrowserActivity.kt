@file:Suppress("DEPRECATION")

package com.esposito.stealth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.esposito.stealth.ui.BrowserControls
import com.esposito.stealth.ui.theme.StealthTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URISyntaxException

class BrowserActivity : ComponentActivity() {
// TODO: implement more search engines
    override fun onCreate(savedInstanceState: Bundle?) {
        StealthUtils.init(this)
        if (StealthSettings.getSetting(this, "should_block_screenshots")) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                setRecentsScreenshotEnabled(false)
            }
        }
        super.onCreate(savedInstanceState)
        setContent {
            StealthTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StartPage(this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartPage(ctx: Context) {
    val url = remember { mutableStateOf("") }
    val btnSearch = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = ctx.getString(R.string.dialog_welcome_homepage), modifier = Modifier.align(Alignment.Center))
        Spacer(modifier = Modifier.height(50.dp))
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = url.value,
                onValueChange = { url.value = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .clip(CircleShape),
                placeholder = { Text(text = ctx.getString(R.string.dialog_url_bar)) },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )
            Button(enabled = url.value != "",
                onClick = {
                    btnSearch.value = true
                }
            ) {
                Icon(Icons.Filled.Search, contentDescription = ctx.getString(R.string.search))
            }
        }
    }
    if (btnSearch.value) {
        WebViewContainer(ctx, UrlUtils.sanitize(url.value))
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContainer(ctx: Context, url: String) {
    val webView = remember { WebView(ctx) }
    val cookieManager: CookieManager = CookieManager.getInstance()
    val isScrolling = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    BackHandler(onBack = {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            StealthUtils.restartApp(ctx)
        }
    })
    DisposableEffect(Unit) {
        onDispose {
            cookieManager.removeSessionCookie()
            cookieManager.setAcceptCookie(false)
            webView.clearCache(true)
            webView.destroy()
            webView.destroy()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { _ ->
                webView.webChromeClient = WebChromeClient()
                webView.apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            view?.setOnScrollChangeListener { _, _, _, _, _ ->
                                isScrolling.value = true
                                scope.launch {
                                    delay(5000L)
                                    isScrolling.value = false
                                }
                            }
                        }
                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            val urlAddress = request?.url.toString()
                            return if (urlAddress.startsWith("intent://")) {
                                try {
                                    val intent = Intent.parseUri(urlAddress, Intent.URI_INTENT_SCHEME)
                                    if (intent != null) {
                                        view?.context?.startActivity(intent)
                                    }
                                    true
                                } catch (e: URISyntaxException) {
                                    false
                                }
                            } else {
                                super.shouldOverrideUrlLoading(view, request)
                            }
                        }
                    }
                    settings.javaScriptEnabled = StealthSettings.getSetting(ctx, "should_enable_js")
                    settings.setGeolocationEnabled(false)
                    settings.setSupportZoom(true)
                    settings.saveFormData = false
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.mediaPlaybackRequiresUserGesture = true
                    settings.allowContentAccess = true
                    settings.pluginState = WebSettings.PluginState.OFF
                    settings.domStorageEnabled = false
                    settings.javaScriptCanOpenWindowsAutomatically = false
                    settings.databaseEnabled = false
                    settings.cacheMode = WebSettings.LOAD_NO_CACHE
                    if (StealthSettings.getSetting(ctx, "should_randomize_user_agent")) {
                        settings.userAgentString = BrowserUtils.getUserAgent()
                    }

                    setDownloadListener { _, _, _, _, _ -> /* Block downloads */ }
                    clearCache(true)
                    clearHistory()
                    clearFormData()
                    cookieManager.setAcceptCookie(false)
                    loadUrl(url)
                }
            }
        )
    }
    Spacer(modifier = Modifier.height(50.dp))
    if (!isScrolling.value) {
        BrowserControls(ctx, webView)
    }
}

