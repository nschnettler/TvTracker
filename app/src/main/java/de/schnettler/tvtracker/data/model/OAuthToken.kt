package de.schnettler.tvtracker.data.model

data class OAuthToken(
    val access_token: String,
    val token_type: String,
    val expires_in: Long,
    val refresh_token: String,
    val created_at: Long
)