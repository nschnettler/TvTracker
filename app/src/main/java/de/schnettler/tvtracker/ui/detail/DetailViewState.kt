package de.schnettler.tvtracker.ui.detail

import com.etiennelenhart.eiffel.state.ViewState
import de.schnettler.tvtracker.data.models.CastEntity
import de.schnettler.tvtracker.data.models.SeasonDomain
import de.schnettler.tvtracker.data.models.ShowDetailDomain
import de.schnettler.tvtracker.data.models.ShowDomain

data class DetailViewState(
    val show: ShowDomain,
    val details: ShowDetailDomain? = null,
    val cast: List<CastEntity>? = null,
    val relatedShows: List<ShowDomain>? = null,
    val seasons: List<SeasonDomain>? = null,
    val expandedSeasons: Set<Long> = setOf()
): ViewState