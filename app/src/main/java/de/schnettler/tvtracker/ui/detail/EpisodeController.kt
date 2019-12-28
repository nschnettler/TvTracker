package de.schnettler.tvtracker.ui.detail

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import de.schnettler.tvtracker.EpisodeBindingModel_
import de.schnettler.tvtracker.data.models.EpisodeWithDetailsDomain

class EpisodeController: PagedListEpoxyController<EpisodeWithDetailsDomain>(){
    override fun buildItemModel(currentPosition: Int, item: EpisodeWithDetailsDomain?): EpoxyModel<*> {
        return EpisodeBindingModel_()
            .id(currentPosition)
            .episode(item?.episode)
            .detail(item?.details)
    }
}