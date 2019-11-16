package de.schnettler.tvtracker.ui.detail

import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.carousel
import de.schnettler.tvtracker.*
import de.schnettler.tvtracker.data.show.model.Show
import de.schnettler.tvtracker.data.show.model.ShowDetails
import de.schnettler.tvtracker.data.show.model.cast.CastEntry
import de.schnettler.tvtracker.util.getEmoji
import de.schnettler.tvtracker.util.withModelsFrom

class DetailController(val show: Show, var showDetails: ShowDetails? = null, var showCast: List<CastEntry>? = null): EpoxyController(){

//    var callbacks: Callbacks? = null
//    interface Callbacks {
//        fun onItemClicked(view: View, item: Show)
//    }

    override fun buildModels() {
        showInfo {
            id("showInfo")
            posterUrl(show.posterUrl)
            showDetails(showDetails)
        }

        showSummary {
            id("showSummary")
            showSummary(showDetails?.overview ?: "No Summary available")
        }

        carousel {
            id("genres")
            withModelsFrom(showDetails?.genres) {
                ShowGenreBindingModel_()
                    .id(it)
                    .title(it)
                    .emoji(getEmoji(it))
            }
            padding(Carousel.Padding.dp(16,8,16,4,8))
        }

        if (!showCast.isNullOrEmpty()) {
            carousel {
                id("cast")
                withModelsFrom(showCast) {
                    CastBindingModel_()
                        .id("${it.id}")
                        .castItem(it)
                }
                padding(Carousel.Padding.dp(16,8,16,4,8))
            }
        }
    }
}