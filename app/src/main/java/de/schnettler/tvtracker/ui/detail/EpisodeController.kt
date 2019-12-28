package de.schnettler.tvtracker.ui.detail

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.carousel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import de.schnettler.tvtracker.EpisodeBindingModel_
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.databinding.ViewHolderEpisodeItemBinding
import timber.log.Timber

class EpisodeController: PagedListEpoxyController<EpisodeDomain>(){
    override fun buildItemModel(currentPosition: Int, item: EpisodeDomain?): EpoxyModel<*> {
        Timber.i("New Item ${item?.title}")
        return EpisodeBindingModel_()
            .id(currentPosition)
            .episode(item)
    }
}