package com.stcc.mystore.network.api

/**
 * Created by Abhilash on 3/22/2022.
 */
class ApiMainHeadersProvider {

    /**
     * Returns both the default headers and the headers that are mandatory for authenticated users.
     */
    fun getAuthenticatedHeaders(): AuthenticatedHeaders =
        AuthenticatedHeaders().apply {
            put(AUTH, getBearer())
        }

    companion object {
        const val AUTH = "Authorization"

        private fun getBearer() = "Bearer ${"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"}"
    }
}

open class ApiMainHeaders : HashMap<String, String>()
class AuthenticatedHeaders : ApiMainHeaders()
