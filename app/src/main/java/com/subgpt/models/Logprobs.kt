package com.subgpt.models

data class Logprobs(
    val text_offset: List<Int>,
    val token_logprobs: List<Double>,
    val tokens: List<String>,
    val top_logprobs: Any
)