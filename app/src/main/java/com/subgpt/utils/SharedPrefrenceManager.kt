package com.subgpt.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.*

class SharedPrefrenceManager(sharedPref: SharedPreferences) {
    private var sharedPreferences: SharedPreferences = sharedPref


    companion object {

        private const val SUB_GPT_PREFERENCE = "SUB_GPT"
        private const val ASSISTANT_NAME = "ASSISTANT_NAME"
        private const val API_KEY = "API_KEY"

        @Volatile
        private var instance: SharedPrefrenceManager? = null

        fun initializeSharePref(context: Context) {
            instance ?: synchronized(SharedPrefrenceManager::class.java) {
                val sharedPref =
                    context.getSharedPreferences(SUB_GPT_PREFERENCE, Context.MODE_PRIVATE)
                instance ?: SharedPrefrenceManager(sharedPref).also {
                    instance = it
                }
            }
        }

        fun getSharedPrefInstance() =
            instance ?: throw RuntimeException("Please Initialize class first")
    }

    var assistantName: String
        get() = (sharedPreferences.getString(ASSISTANT_NAME, "Assistant") ?: "")
        set(city) {
            sharedPreferences.edit {
                putString(ASSISTANT_NAME, city)
            }
        }

    var apiKey: String
        get() = (sharedPreferences.getString(API_KEY, "") ?: "")
        set(city) {
            sharedPreferences.edit {
                putString(API_KEY, city)
            }
        }

}