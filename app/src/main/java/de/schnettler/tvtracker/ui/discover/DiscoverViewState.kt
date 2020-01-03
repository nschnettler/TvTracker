package de.schnettler.tvtracker.ui.discover

import com.etiennelenhart.eiffel.state.ViewState
import de.schnettler.tvtracker.data.models.*

data class DiscoverViewState(
    var trendingShows: List<ShowDomain>? = generatePlaceholderShows(10),
    var popularShows: List<ShowDomain>? = generatePlaceholderShows(10),
    var anticipatedShows: List<ShowDomain>? = generatePlaceholderShows(10),
    var recommendedShows: List<ShowDomain>? = generatePlaceholderShows(10),
    var loggedIn: Boolean = false
): ViewState

fun generatePlaceholderShows(number: Int): List<ShowDomain> {
    val result = mutableListOf<ShowDomain>()
    for (i in 0L until number) {
        result.add(ShowDomain(i, true))
    }
    return result
}