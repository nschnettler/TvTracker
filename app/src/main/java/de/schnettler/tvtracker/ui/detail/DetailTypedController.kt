package de.schnettler.tvtracker.ui.detail

import android.view.View
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.carousel
import de.schnettler.tvtracker.*
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.data.models.SeasonDomain
import de.schnettler.tvtracker.util.getEmoji
import de.schnettler.tvtracker.util.isoToDate
import de.schnettler.tvtracker.util.withModelsFrom
import timber.log.Timber

class DetailTypedController: TypedEpoxyController<DetailViewState>() {
    var callbacks: Callbacks? = null
    interface Callbacks {
        fun onShowClicked(view: View, item: ShowDomain)
        fun onSeasonClicked(season: SeasonDomain, isExpanded: Boolean)
        fun onEpisodeClicked(episodeId: Long)
    }

    override fun buildModels(data: DetailViewState?) {

        data?.let {
            val show = data.show
            val showDetails = data.details
            val showCast = data.cast
            val showRelated = data.relatedShows
            val seasons = data.seasons
            val expandedSeasons = data.expandedSeasons

            showDetails?.let {
                //Show Info
                showInfo {
                    id("showInfo")
                    posterUrl(show.posterUrl)
                    showDetails(it)
                    status(
                        when(it.status) {
                            "in production" -> "Upcoming • ${isoToDate(it.firstAired)}"
                            "returning series" -> "Running"
                            "ended" -> "Completed"
                            else -> it.status
                        }
                    )
                    rating("${it.rating}%")
                    runtime("${it.runtime} min")
                }

                //Show Summary
                showSummary {
                    id("showSummary")
                    showSummary(it.overview)
                }

                //Genres
                carousel {
                    id("genres")
                    withModelsFrom(it.genres) {
                        ShowGenreBindingModel_()
                            .id(it)
                            .title(it)
                            .emoji(getEmoji(it))
                    }
                    padding(Carousel.Padding.dp(16,8,16,8,8))
                }
            }

            showCast?.let {
                header {
                    id("cast_header")
                    title("Cast")
                    showExpand(false)
                }
                carousel {
                    id("cast")
                    withModelsFrom(showCast) {
                        CastBindingModel_()
                            .id("${it.id}")
                            .castItem(it)
                    }
                    padding(Carousel.Padding.dp(16,8,16,16,8))
                }
            }

            seasons?.let {
                header {
                    id("episodes_header")
                    title("Seasons")
                    showExpand(false)
                }
                seasons.forEach {season ->
                    twoLineList {
                        id(season.id)
                        header(season.title)
                        description("${season.episodeCount} Episodes • ${season.rating}% Rating")
                        imageText("${season.number}")
                        onClickListener { _, _, _, _ ->
                            callbacks?.onSeasonClicked(season, expandedSeasons.contains(season.id))
                        }
                    }
                    if (expandedSeasons.contains(season.id)) {
                        season.episodes?.forEach {episode ->
                            episodeItem {
                                id(episode.id)
                                header(episode.title)
                                imageText("#${episode.number}")
                                onClickListener { _, _, _, _ ->
                                    callbacks?.onEpisodeClicked(episode.id)
                                }
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
                            .onClickListener{ _, _, view, _ ->
                                Timber.i("Clicked on ${it.title}")
                                callbacks?.onShowClicked(view, it)
                            }
                    }
                    padding(Carousel.Padding.dp(16,8,16,16,8))
                }
            }
        }
    }
}