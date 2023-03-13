package com.stcc.mystore.network.repository

import com.subgpt.models.BaseMessageSender
import com.stcc.mystore.network.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun chatGPT(msg: BaseMessageSender) = apiHelper.ChatGPT(msg)
}