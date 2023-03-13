package com.stcc.mystore.network.api

import com.subgpt.models.BaseMessageSender

class ApiHelper(private val apiService: ApiService) {

    var headersProvider = ApiMainHeadersProvider()

    suspend fun ChatGPT(msg: BaseMessageSender) = apiService.ChatGPT(
        headersProvider.getAuthenticatedHeaders(), msg
    )

}