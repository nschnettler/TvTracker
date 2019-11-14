package de.schnettler.tvtracker.data

import de.schnettler.tvtracker.data.model.Show

data class DiscoverViewState(
    val trendingShows: List<Show>?,
    val popularShows: List<Show>?
)