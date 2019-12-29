package de.schnettler.tvtracker.ui.detail

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import de.schnettler.tvtracker.EpisodeBindingModel_
import de.schnettler.tvtracker.data.models.EpisodeFullDomain

class EpisodeController: PagedListEpoxyController<EpisodeFullDomain>(){
    override fun buildItemModel(currentPosition: Int, item: EpisodeFullDomain?): EpoxyModel<*> {
        return EpisodeBindingModel_()
            .id(currentPosition)
            .episode(item)
    }
}