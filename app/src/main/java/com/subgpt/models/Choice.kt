package com.subgpt.models

data class Choice(
    val finish_reason: String,
    val index: Int,
    val message: Message
)

data class Message(
    val content: String,
    val role: String
)