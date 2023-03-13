package com.subgpt.models

class BaseMessageSender {
    var best_of: Int? = null
    var echo: Boolean? = null
    val frequency_penalty: Int? = null
    val logprobs: Int? = null
    var max_tokens: Int? = null
    var model: String? = null
    val presence_penalty: Int? = null
    var prompt: String? = null
    var stream: Boolean? = null
    val temperature: Int? = null
    var top_p: Int? = null

    var messages: List<Message>?=null


}