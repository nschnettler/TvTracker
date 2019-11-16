package de.schnettler.tvtracker.ui.detail

import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.carousel
import de.schnettler.tvtracker.CastBindingModel_
import de.schnettler.tvtracker.ShowGenreBindingModel_
import de.schnettler.tvtracker.showInfo
import de.schnettler.tvtracker.showSummary
import de.schnettler.tvtracker.util.getEmoji
import de.schnettler.tvtracker.util.withModelsFrom

class DetailTypedController: TypedEpoxyController<DetailViewState>() {
    override fun buildModels(data: DetailViewState?) {

        data?.let {
            val show = data.show
            val showDetails = data.showDetails
            val showCast = data.showCast

            //Show Info
            showInfo {
                id("showInfo")
                posterUrl(show.posterUrl)
                showDetails(showDetails)
            }

            //Show Summary
            showSummary {
                id("showSummary")
                showSummary(showDetails?.overview)
            }

            showDetails?.let {
                //Genres
                carousel {
                    id("genres")
                    withModelsFrom(showDetails.genres) {
                        ShowGenreBindingModel_()
                            .id(it)
                            .title(it)
                            .emoji(getEmoji(it))
                    }
                    padding(Carousel.Padding.dp(16,8,16,4,8))
                }
            }

            showCast?.let {
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
}