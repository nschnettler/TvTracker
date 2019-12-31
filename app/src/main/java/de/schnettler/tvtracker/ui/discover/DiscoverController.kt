package de.schnettler.tvtracker.ui.discover

import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.carousel
import de.schnettler.tvtracker.ShowSmallBindingModel_
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.header
import de.schnettler.tvtracker.util.withModelsFrom
import timber.log.Timber

class DiscoverController: TypedEpoxyController<DiscoverViewState>() {
    var callbacks: Callbacks? = null
    interface Callbacks {
        fun onItemClicked(view: View, item: ShowDomain)
    }

    override fun buildModels(data: DiscoverViewState?) {

        data?.let {
            header {
                id("header_trending")
                title("Trending now")
                showExpand(true)
            }

            data.trendingShows?.let {shows ->
                carousel {
                    id("trending")
                    withModelsFrom(shows) {
                        ShowSmallBindingModel_()
                            .id("trending_$it.id")
                            .title(it.title)
                            .posterUrl(it.posterUrl)
                            .transitionName("trending_${it.id}")
                            .onClickListener{ _, _, view, _ ->
                                Timber.i("Clicked on ${it.title}")
                                callbacks?.onItemClicked(view, it)
                            }
                    }
                }
            }

            if (data.trendingShows == null) {
                carousel {
                    id("trending_placeholder")
                    withModelsFrom(listOf(1, 2, 3, 4)) {
                        ShowSmallBindingModel_()
                            .id("trending_placeholder_$it")
                    }
                }
            }


            header {
                id("header_recommended")
                title("For you")
                showExpand(true)
            }
            data.recommendedShows?.let {shows ->
                carousel {
                    id("recommended")
                    withModelsFrom(shows) {
                        ShowSmallBindingModel_()
                            .id(it.id)
                            .title(it.title)
                            .posterUrl(it.posterUrl)
                            .transitionName("recommended_${it.id}")
                            .onClickListener{ _, _, view, _ ->
                                Timber.i("Clicked on ${it.title}")
                                callbacks?.onItemClicked(view, it)
                            }
                    }
                }
            }

            if (data.recommendedShows == null) {
                carousel {
                    id("recommendedShows_placeholder")
                    withModelsFrom(listOf(1, 2, 3, 4)) {
                        ShowSmallBindingModel_()
                            .id("recommendedShows_placeholder_$it")
                    }
                }
            }


            header {
                id("header_popular")
                title("Popular")
                showExpand(true)
            }
            data.popularShows?.let {shows ->
                carousel {
                    id("popular")
                    withModelsFrom(shows) {
                        ShowSmallBindingModel_()
                            .id(it.id)
                            .title(it.title)
                            .posterUrl(it.posterUrl)
                            .transitionName("popular_${it.id}")
                            .onClickListener{ _, _, view, _ ->
                                Timber.i("Clicked on ${it.title}")
                                callbacks?.onItemClicked(view, it)
                            }
                    }
                }
            }

            if (data.popularShows == null) {
                carousel {
                    id("popularShows_placeholder")
                    withModelsFrom(listOf(1, 2, 3, 4)) {
                        ShowSmallBindingModel_()
                            .id("popularShows_placeholder_$it")
                    }
                }
            }

            header {
                id("header_anticipated")
                title("Most Anticipated")
                showExpand(true)
            }
            data.anticipatedShows?.let {shows ->
                carousel {
                    id("anticipated")
                    withModelsFrom(shows) {
                        ShowSmallBindingModel_()
                            .id(it.id)
                            .title(it.title)
                            .posterUrl(it.posterUrl)
                            .transitionName("anticipated_${it.id}")
                            .onClickListener{ _, _, view, _ ->
                                Timber.i("Clicked on ${it.title}")
                                callbacks?.onItemClicked(view, it)
                            }
                    }
                }
            }

            if (data.anticipatedShows == null) {
                carousel {
                    id("anticipatedShows_placeholder")
                    withModelsFrom(listOf(1, 2, 3, 4)) {
                        ShowSmallBindingModel_()
                            .id("anticipatedShows_placeholder_$it")
                    }
                }
            }
        }
    }
}