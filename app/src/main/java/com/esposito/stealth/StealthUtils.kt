package com.esposito.stealth

import android.app.Activity
import android.content.Context
import android.content.Intent

object StealthUtils {

    fun restartApp(ctx: Context) {
        val intent = Intent(ctx, BrowserActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        (ctx as Activity).finish()
        ctx.startActivity(intent)
    }

    fun init(ctx: Context) {
        if (isFirstTimeLaunch(ctx)) { StealthSettings.initSettings(ctx) }
    }

    private fun isFirstTimeLaunch(ctx: Context): Boolean {
        val prefs = ctx.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)
        return if (isFirstLaunch) {
            with (prefs.edit()) {
                putBoolean("isFirstLaunch", false)
                apply()
            }
            true
        } else {
            false
        }
    }
}