package de.schnettler.tvtracker.ui.detail

import com.etiennelenhart.eiffel.state.ViewState
import de.schnettler.tvtracker.data.auth.model.AuthTokenDB
import de.schnettler.tvtracker.data.show.model.Show
import de.schnettler.tvtracker.data.show.model.ShowDetails
import de.schnettler.tvtracker.data.show.model.cast.CastEntry

data class DetailViewState(
    val show: Show,
    val showDetails: ShowDetails? = null,
    val tvdbAuth: AuthTokenDB? = null,
    val showCast: List<CastEntry>? = null
): ViewState