package de.schnettler.tvtracker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import de.schnettler.tvtracker.data.api.TVDB

/*
 * Generic Authentication Response
 */
sealed class AuthTokenResponse(
    open val token: String,
    open val refreshToken: String?,
    open val createdAt: Long,
    open val type: AuthTokenType
)

/*
 * Trakt Authentication Response
 */
data class TraktAuthTokenResponse(
    @Json(name = "access_token") override val token: String,
    @Json(name = "refresh_token") override val refreshToken: String,
    @Json(name = "created_at") override val createdAt: Long
): AuthTokenResponse(token, refreshToken, createdAt, AuthTokenType.TRAKT)


/*
 * TVDB Authentication Response
 */
data class TvdbAuthTokenResponse(
    override val token: String
): AuthTokenResponse(token, null,System.currentTimeMillis() / 1000L, AuthTokenType.TVDB)


/*
 * Generic Authentication Result in Database
 */
@Entity(tableName = "table_auth")
data class AuthTokenEntity(
    @PrimaryKey val tokenName:String,
    val token: String,
    val refreshToken: String?,
    val createdAtMillis: Long
)


/*
 * Generic Authentication Result in Domain
 */
data class AuthTokenDomain(
    val type: AuthTokenType,
    val token: String,
    val refreshToken: String?,
    val createdAtMillis: Long
)

enum class AuthTokenType(val value: String) {
    TVDB("tvdb_token"),
    TRAKT("trakt_token")
}

data class TvdbLoginData(val apikey: String = TVDB.API_KEY, val username: String = "", val userpass: String = "")