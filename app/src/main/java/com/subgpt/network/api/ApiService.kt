package com.stcc.mystore.network.api

import com.subgpt.models.BaseMessageReceiver
import com.subgpt.models.BaseMessageSender
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST


interface ApiService {

    @POST("v1/chat/completions")
    suspend fun ChatGPT(
        @HeaderMap publicHeaders: AuthenticatedHeaders,
        @Body commonCMSBuilder: BaseMessageSender
    ): Response<BaseMessageReceiver>
}