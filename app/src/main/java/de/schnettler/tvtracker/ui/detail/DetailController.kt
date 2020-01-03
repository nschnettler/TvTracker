package de.schnettler.tvtracker.ui.detail

import android.view.View
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.carousel
import de.schnettler.tvtracker.*
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.data.models.SeasonDomain
import de.schnettler.tvtracker.util.getEmoji
import de.schnettler.tvtracker.util.horizontalCarousel
import de.schnettler.tvtracker.util.isoToDate
import de.schnettler.tvtracker.util.withModelsFrom
import timber.log.Timber

class DetailController : TypedEpoxyController<DetailViewState>() {
    var callbacks: Callbacks? = null

    interface Callbacks {
        fun onShowClicked(view: View, item: ShowDomain)
        fun onSeasonClicked(season: SeasonDomain, isExpanded: Boolean)
        fun onEpisodeClicked(episode: EpisodeDomain)
    }

    override fun buildModels(data: DetailViewState) {

        val show = data.show
        val showDetails = data.details
        val showCast = data.cast
        val showRelated = data.relatedShows
        val seasons = data.seasons
        val expandedSeasons = data.expandedSeasons


        showInfo {
            id("showInfo")
            posterUrl(show.posterUrl)
            showDetails(showDetails)
            status(
                when (showDetails?.status) {
                    "in production" -> "Upcoming • ${isoToDate(showDetails.firstAired)}"
                    "returning series" -> "Running"
                    "ended" -> "Completed"
                    else -> showDetails?.status
                }
            )
            rating("${showDetails?.rating}%")
            runtime("${showDetails?.runtime} min")
        }

        //Show Summary
        showSummary {
            id("showSummary")
            showSummary(showDetails?.overview)
        }

        //Genres
        showDetails?.let {details ->
            horizontalCarousel {
                id("genres")
                withModelsFrom(details.genres) {
                    ShowGenreBindingModel_()
                        .id(it)
                        .title(it)
                        .emoji(getEmoji(it))
                }
                padding(Carousel.Padding.dp(16, 8, 16, 8, 8))
            }
        }

        showCast?.let { cast ->
            header {
                id("cast_header")
                title("Cast")
                showExpand(false)
            }
            horizontalCarousel {
                id("cast")
                withModelsFrom(cast) {
                    CastBindingModel_()
                        .id("${it.id}")
                        .castItem(it)
                }
                padding(Carousel.Padding.dp(16, 8, 16, 16, 8))
            }
        }


        header {
            id("episodes_header")
            title("Seasons")
            showExpand(false)
        }
        seasons?.forEach { season ->
            twoLineList {
                id("s_${season.number}")
                header(season.title)
                description("${season.firstAired} • ${season.episodeCount} • ${season.rating}%")
                imageText("${season.number}")
                onClickListener { _, _, _, _ ->
                    callbacks?.onSeasonClicked(season, expandedSeasons.contains(season.number))
                }
            }
            if (expandedSeasons.contains(season.number)) {
                season.episodes?.forEach { episode ->
                    episodeItem {
                        id("s_${season.number}_e${episode.number}")
                        header(episode.title)
                        imageText("#${episode.number}")
                        onClickListener { _, _, _, _ ->
                            callbacks?.onEpisodeClicked(episode)
                        }
                    }
                }
            }
        }

        showRelated?.let {
            header {
                id("related_header")
                title("Related Shows")
                showExpand(false)
            }
            carousel {
                id("related")
                withModelsFrom(showRelated) {
                    ShowMiniBindingModel_()
                        .id(it.id)
                        .title(it.title)
                        .posterUrl(it.posterUrl)
                        .transitionName("related_${it.id}")
                        .onClickListener { _, _, view, _ ->
                            Timber.i("Clicked on ${it.title}")
                            callbacks?.onShowClicked(view, it)
                        }
                }
                padding(Carousel.Padding.dp(16, 8, 16, 16, 8))
            }
        }
    }
}