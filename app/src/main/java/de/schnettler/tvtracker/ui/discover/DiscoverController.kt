package de.schnettler.tvtracker.ui.discover

import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.carousel
import de.schnettler.tvtracker.ShowSmallBindingModel_
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.header
import de.schnettler.tvtracker.util.withModelsFrom

class DiscoverController : TypedEpoxyController<DiscoverViewState>() {
    var callbacks: Callbacks? = null

    interface Callbacks {
        fun onItemClicked(view: View, item: ShowDomain)
    }

    override fun buildModels(data: DiscoverViewState) {

        //Trending
        header {
            id("header_trending")
            title("Trending now")
            showExpand(true)
        }
        carousel {
            id("trending")
            withModelsFrom(data.trendingShows) {
                ShowSmallBindingModel_()
                    .id("trending_$it.id")
                    .title(it.title)
                    .posterUrl(it.posterUrl)
                    .transitionName("trending_${it.id}")
                    .onClickListener { _, _, view, _ ->
                        if(!it.placeholder) { callbacks?.onItemClicked(view, it) }
                    }
            }
        }

        //Recommended
        if(data.loggedIn) {
            header {
                id("header_recommended")
                title("For you")
                showExpand(true)
            }
            carousel {
                id("recommended")
                withModelsFrom(data.recommendedShows) {
                    ShowSmallBindingModel_()
                        .id(it.id)
                        .title(it.title)
                        .posterUrl(it.posterUrl)
                        .transitionName("recommended_${it.id}")
                        .onClickListener { _, _, view, _ ->
                            if(!it.placeholder) { callbacks?.onItemClicked(view, it) }
                        }
                }
            }
        }

        //Popular
        header {
            id("header_popular")
            title("Popular")
            showExpand(true)
        }
        carousel {
            id("popular")
            withModelsFrom(data.popularShows) {
                ShowSmallBindingModel_()
                    .id(it.id)
                    .title(it.title)
                    .posterUrl(it.posterUrl)
                    .transitionName("popular_${it.id}")
                    .onClickListener { _, _, view, _ ->
                        if(!it.placeholder) { callbacks?.onItemClicked(view, it) }
                    }
            }
        }

        //Anticipated
        header {
            id("header_anticipated")
            title("Most Anticipated")
            showExpand(true)
        }
        carousel {
            id("anticipated")
            withModelsFrom(data.anticipatedShows) {
                ShowSmallBindingModel_()
                    .id(it.id)
                    .title(it.title)
                    .posterUrl(it.posterUrl)
                    .transitionName("anticipated_${it.id}")
                    .onClickListener { _, _, view, _ ->
                        if(!it.placeholder) { callbacks?.onItemClicked(view, it) }
                    }
            }
        }
    }
}