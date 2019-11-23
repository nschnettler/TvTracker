package de.schnettler.tvtracker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.schnettler.tvtracker.data.api.TVDB

data class OAuthToken(
    val access_token: String,
    val token_type: String,
    val expires_in: Long,
    val refresh_token: String,
    val created_at: Long
)

data class TvdbAuthTokenResponse(
    val token: String
) {
    fun toAuthTokenDB(tokenType: AuthTokenType) =
        AuthTokenDB(
            tokenName = tokenType.value,
            token = token,
            createdAtMillis = System.currentTimeMillis() / 1000L
        )
}

@Entity(tableName = "table_auth")
data class AuthTokenDB(
    @PrimaryKey val tokenName:String,
    val token: String,
    val createdAtMillis: Long
)

enum class AuthTokenType(val value: String) {
    TVDB("tvdb_token"),
    TRAKT("trakt_token")
}

data class TvdbLoginData(val apikey: String = TVDB.API_KEY, val username: String = "", val userpass: String = "")