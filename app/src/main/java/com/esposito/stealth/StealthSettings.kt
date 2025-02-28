package com.esposito.stealth

import android.content.Context

object StealthSettings{
    fun applySettings(ctx: Context, shouldEnableJS: Boolean, shouldBlockScreenshot: Boolean, shouldRandomizeUserAgent: Boolean) {
        SharedPrefsUtils.saveBoolean(ctx, "should_enable_js", shouldEnableJS)
        SharedPrefsUtils.saveBoolean(ctx, "should_block_screenshots", shouldBlockScreenshot)
        SharedPrefsUtils.saveBoolean(ctx, "should_randomize_user_agent", shouldRandomizeUserAgent)
    }

    fun initSettings(ctx: Context){
        SharedPrefsUtils.saveBoolean(ctx, "should_enable_js", true)
        SharedPrefsUtils.saveBoolean(ctx, "should_block_screenshots", false)
        SharedPrefsUtils.saveBoolean(ctx, "should_randomize_useragent", true)
    }

    fun getSetting(ctx: Context, name: String): Boolean {
        return SharedPrefsUtils.getBoolean(ctx, name)
    }
}