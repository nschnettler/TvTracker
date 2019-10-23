package de.schnettler.tvtracker.data.model

data class Show(
    val id: Long,
    val title: String,
    var posterUrl: String = ""
)