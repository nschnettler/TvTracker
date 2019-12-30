package de.schnettler.tvtracker.ui.discover

import com.etiennelenhart.eiffel.state.ViewState
import de.schnettler.tvtracker.data.models.CastEntity
import de.schnettler.tvtracker.data.models.SeasonDomain
import de.schnettler.tvtracker.data.models.ShowDetailDomain
import de.schnettler.tvtracker.data.models.ShowDomain

data class DiscoverViewState(
    var trendingShows: List<ShowDomain>? = null,
    var popularShows: List<ShowDomain>? = null,
    var anticipatedShows: List<ShowDomain>? = null,
    var recommendedShows: List<ShowDomain>? = null
): ViewState