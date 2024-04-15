package com.esposito.stealth

import android.content.Context

object StealthSettings{
    fun applySettings(ctx: Context, shouldEnableJS: Boolean, shouldBlockScreenshot: Boolean) {
        SharedPrefsUtils.saveBoolean(ctx, "should_enable_js", shouldEnableJS)
        SharedPrefsUtils.saveBoolean(ctx, "should_block_screenshots", shouldBlockScreenshot)
    }

    fun initSettings(ctx: Context){
        SharedPrefsUtils.saveBoolean(ctx, "should_enable_js", true)
        SharedPrefsUtils.saveBoolean(ctx, "should_block_screenshots", false)
    }

    fun getSetting(ctx: Context, name: String): Boolean {
        return SharedPrefsUtils.getBoolean(ctx, name)
    }
}