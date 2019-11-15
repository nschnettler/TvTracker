package de.schnettler.tvtracker.data.person.model

data class CastEntry(
    val character: String,
    val actor: String,
    val actorId: Long,
    val posterUrl: String?
)