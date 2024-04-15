package com.esposito.stealth

import android.content.Context

object SharedPrefsUtils {

    private const val PREF_NAME = "stealth_settings"

    fun saveBoolean(context: Context, key: String, value: Boolean) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(context: Context, key: String): Boolean {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(key, false)
    }
}