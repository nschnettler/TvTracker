package de.schnettler.tvtracker.ui.detail

import com.etiennelenhart.eiffel.state.ViewState
import de.schnettler.tvtracker.data.auth.model.AuthTokenDB
import de.schnettler.tvtracker.data.show.model.Show
import de.schnettler.tvtracker.data.show.model.ShowDetails
import de.schnettler.tvtracker.data.show.model.cast.CastEntry
import de.schnettler.tvtracker.data.show.model.season.SeasonDomain

data class DetailViewState(
    val show: Show,
    val details: ShowDetails? = null,
    val tvdbAuth: AuthTokenDB? = null,
    val cast: List<CastEntry>? = null,
    val relatedShows: List<Show>? = null,
    val seasons: List<SeasonDomain>? = null,
    val expandedSeasons: Set<Long> = setOf()
): ViewState