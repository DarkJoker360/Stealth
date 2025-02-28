package com.esposito.stealth

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.esposito.stealth.ui.theme.StealthTheme

class SettingsActivity : ComponentActivity() {
    private val ctx: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (StealthSettings.getSetting(this, "should_block_screenshots")) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
        setContent {
            StealthTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Header(getString(R.string.settings))
                        Settings(ctx)
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(text: String) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)) {
        Text(
            text,
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.headlineLarge,
            maxLines = 1
        )
    }
}

@Composable
fun Settings(ctx: Context) {
    var shouldEnableJS by remember { mutableStateOf(StealthSettings.getSetting(ctx, "should_enable_js")) }
    var shouldBlockScreenshot by remember { mutableStateOf(StealthSettings.getSetting(ctx, "should_block_screenshots")) }
    var shouldRandomizeUserAgent by remember { mutableStateOf(StealthSettings.getSetting(ctx, "should_randomize_user_agent")) }

    Row (modifier = Modifier.fillMaxWidth()) {
        Text(ctx.getString(R.string.settings_enable_js),
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(5.dp))
        Switch(
            checked = shouldEnableJS,
            onCheckedChange = { shouldEnableJS = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        )
    }
    Row (modifier = Modifier.fillMaxWidth()) {
        Text(ctx.getString(R.string.settings_block_screenshots),
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(5.dp))
        Switch(
            checked = shouldBlockScreenshot,
            onCheckedChange = { shouldBlockScreenshot = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        )
    }
    Row (modifier = Modifier.fillMaxWidth()) {
        Text("Randomize User Agent",
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(5.dp))
        Switch(
            checked = shouldRandomizeUserAgent,
            onCheckedChange = { shouldRandomizeUserAgent = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        )
    }
    Box(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row {
            Button(onClick = {
                shouldEnableJS = true
                shouldBlockScreenshot = false
                shouldRandomizeUserAgent = true
                StealthSettings.initSettings(ctx)
                Toast.makeText(ctx, ctx.getString(R.string.dialog_settings_applied), Toast.LENGTH_SHORT).show()
                StealthUtils.restartApp(ctx)
            }) {
                Text(ctx.getString(R.string.reset))
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(onClick = {
                StealthSettings.applySettings(ctx, shouldEnableJS, shouldBlockScreenshot, shouldRandomizeUserAgent)
                Toast.makeText(ctx, ctx.getString(R.string.dialog_settings_applied), Toast.LENGTH_SHORT).show()
                StealthUtils.restartApp(ctx)
            }) {
                Text(ctx.getString(R.string.save))
            }
        }
    }
}


