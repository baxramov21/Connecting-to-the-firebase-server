package com.template.data.db

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {
    private const val PREF_FILE_NAME = "app_preferences"
    private const val KEY_RESULT = "result"

    fun saveUrl(context: Context, link: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(KEY_RESULT, link)
        editor.apply()
    }

    fun getUrl(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_RESULT, null)
    }
}