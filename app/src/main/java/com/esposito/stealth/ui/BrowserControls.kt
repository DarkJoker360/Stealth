package com.esposito.stealth.ui

import android.content.Context
import android.content.Intent
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.esposito.stealth.R
import com.esposito.stealth.SettingsActivity
import com.esposito.stealth.StealthUtils

@Composable
fun BrowserControls(ctx: Context, webView: WebView) {
    Box(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.background(MaterialTheme.colorScheme.background, CircleShape),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                if (webView.canGoBack()) {
                    webView.goBack()
                }
            }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = ctx.getString(R.string.back))
            }
            IconButton(onClick = {
                if (webView.canGoForward()) {
                    webView.goForward()
                }
            }) {
                Icon(Icons.Filled.ArrowForward, contentDescription = ctx.getString(R.string.next))
            }
            IconButton(onClick = {
                webView.reload()
            }) {
                Icon(Icons.Filled.Refresh, contentDescription = ctx.getString(R.string.reload))
            }
            IconButton(onClick = {
                StealthUtils.restartApp(ctx)
            }) {
                Icon(Icons.Filled.Home, contentDescription = ctx.getString(R.string.home))
            }
            IconButton(onClick = {
                val intent = Intent(ctx, SettingsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                ctx.startActivity(intent)
            }) {
                Icon(Icons.Filled.Settings, contentDescription = ctx.getString(R.string.settings))
            }
        }
    }
}