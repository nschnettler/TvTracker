package de.schnettler.tvtracker.data.model

data class CastEntry(
    val character: String,
    val actor: String,
    val actorId: Long,
    val posterUrl: String?
)