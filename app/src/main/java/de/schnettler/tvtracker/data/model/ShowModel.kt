package de.schnettler.tvtracker.data.model

data class Show(
    val title: String,
    val traktId: Long,
    val tmdbId: Long,
    val year: Long,
    val watchers: Long
)