package com.stcc.mystore.network.api

import com.subgpt.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


object RetrofitBuilder {
    val apiService: ApiService = getRetrofit().create(ApiService::class.java)

    private val OKhttpClientBuilder: OkHttpClient.Builder
        get() = OkHttpClient.Builder()
            .hostnameVerifier { _, _ ->
                return@hostnameVerifier true // verify any host name (FOR QC AND TESTING ONLY)
            }


    fun unSafeOkHttpClient(): OkHttpClient.Builder {
        val okHttpClient = OkHttpClient.Builder()
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            if (trustAllCerts.isNotEmpty() && trustAllCerts.first() is X509TrustManager) {
                okHttpClient.sslSocketFactory(
                    sslSocketFactory,
                    trustAllCerts.first() as X509TrustManager
                )
                okHttpClient.hostnameVerifier { _, _ -> return@hostnameVerifier true }
            }


            return okHttpClient
        } catch (e: Exception) {
            return okHttpClient
        }
    }

    fun createOkHttpClient(vararg interceptors: Interceptor): OkHttpClient {
//        val certificatePinner: CertificatePinner = CertificatePinner.Builder()
//            .add("*.mystore.com.sa", "sha256/Lc9Nszxelw7xD3nD937SmI8ejAAZUVucCt4XER1hAd8=")
//            .add("*.mystore.com.sa", "sha256/RQeZkB42znUfsDIIFWIRiYEcKl7nHwNFwWCrnMMJbVc=")
//            .build()

//        val authenticator = TokenAuthenticator().authenticate()

        try {
            val platform = "android/" + BuildConfig.VERSION_NAME
            val builder = unSafeOkHttpClient()
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)

                .addInterceptor { chain ->
                    val original = chain.request()
                    val builder = original.newBuilder()
                        .header("User-Agent", platform)
                    val request = builder.build()
                    chain.proceed(request)
                }
            for (interceptor in interceptors) {
                builder.addNetworkInterceptor(interceptor)
            }
//            builder.certificatePinner(certificatePinner)
            return builder.build()

        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun getRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client = createOkHttpClient(interceptor)

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl("https://api.openai.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

}