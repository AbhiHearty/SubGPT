package com.subgpt

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.logging.HttpLoggingInterceptor


class MainApp : Application() {
    val TAG = "MainApp"

    init {

        instance = this
    }

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    companion object {

        val gson: Gson by lazy {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.excludeFieldsWithoutExposeAnnotation()
            gsonBuilder.create()
        }

        private var instance: MainApp? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext);
        checkInternet()
    }

    private fun checkInternet() {
//        if (!isNetworkAvailable(this)) {
//            startActivity(
//                Intent(
//                    this,
//                    NoInternetActivity::class.java
//                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            )
//        }
    }


}