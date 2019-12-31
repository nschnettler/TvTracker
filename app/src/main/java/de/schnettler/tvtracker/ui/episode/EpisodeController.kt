package de.schnettler.tvtracker.ui.episode

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import de.schnettler.tvtracker.EpisodeBindingModel_
import de.schnettler.tvtracker.data.models.EpisodeDomain

class EpisodeController: PagedListEpoxyController<EpisodeDomain>(){
    override fun buildItemModel(currentPosition: Int, item: EpisodeDomain?): EpoxyModel<*> {
        return EpisodeBindingModel_()
            .id(currentPosition)
            .episode(item)
    }
}