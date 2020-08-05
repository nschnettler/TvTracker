package de.schnettler.tvtracker.data.api

import javax.inject.Inject

open class ApiHeaders : HashMap<String, String>()
class AuthHeaders : ApiHeaders()
class PublicHeaders : ApiHeaders()

open class HeaderProvider @Inject constructor() {
    companion object {
        private const val AUTHORIZATION = "Authorization"
        const val CONTENT_TYPE = "Content-type"

        private fun getBearer(accessToken: String) = "Bearer $accessToken"
    }

    fun getAuthenticatedHeaders(accessToken: String): AuthHeaders =
        AuthHeaders().apply {
            put(AUTHORIZATION, getBearer(accessToken))
        }

    /**
     * Public headers for calls that do not need an authenticated user.
     */
    fun getPublicHeaders(): PublicHeaders =
        PublicHeaders().apply {
            putAll(defaultHeaders())
        }

    /**
     * Default headers used on all calls.
     */
    open fun defaultHeaders() = mapOf<String, String>()
}

