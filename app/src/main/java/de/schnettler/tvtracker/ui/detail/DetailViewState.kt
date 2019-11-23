package de.schnettler.tvtracker.ui.detail

import com.etiennelenhart.eiffel.state.ViewState
import de.schnettler.tvtracker.data.models.AuthTokenDB
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.data.models.ShowDetailDomain
import de.schnettler.tvtracker.data.models.CastEntity
import de.schnettler.tvtracker.data.models.SeasonDomain

data class DetailViewState(
    val show: ShowDomain,
    val details: ShowDetailDomain? = null,
    val tvdbAuth: AuthTokenDB? = null,
    val cast: List<CastEntity>? = null,
    val relatedShows: List<ShowDomain>? = null,
    val seasons: List<SeasonDomain>? = null,
    val expandedSeasons: Set<Long> = setOf()
): ViewState