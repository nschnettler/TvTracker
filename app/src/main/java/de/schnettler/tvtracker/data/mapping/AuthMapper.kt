package de.schnettler.tvtracker.data.mapping

import de.schnettler.tvtracker.data.models.AuthTokenDomain
import de.schnettler.tvtracker.data.models.AuthTokenEntity
import de.schnettler.tvtracker.data.models.AuthTokenResponse
import de.schnettler.tvtracker.data.models.AuthTokenType

object AuthMapper: Mapper<AuthTokenResponse, AuthTokenEntity, AuthTokenDomain> {
    override fun mapToDatabase(input: AuthTokenResponse) = AuthTokenEntity(
        tokenName = input.type.value,
        token = input.token,
        refreshToken = input.refreshToken,
        createdAtMillis = input.createdAt
    )

    override fun mapToDomain(input: AuthTokenEntity) = AuthTokenDomain(
        token = input.token,
        refreshToken = input.refreshToken,
        createdAtMillis = input.createdAtMillis,
        type = AuthTokenType.valueOf(input.tokenName)
    )
}