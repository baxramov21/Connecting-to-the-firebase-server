package com.template.ui.view_model

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {
    private const val PREF_FILE_NAME = "app_preferences"
    private const val KEY_RESULT = "result"

    fun saveResult(context: Context, result: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(KEY_RESULT, result)
        editor.apply()
    }

    fun getResult(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_RESULT, null)
    }
}