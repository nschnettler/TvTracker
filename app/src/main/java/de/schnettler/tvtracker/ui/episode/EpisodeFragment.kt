package de.schnettler.tvtracker.ui.episode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.tvtracker.R
import de.schnettler.tvtracker.databinding.EpisodeBottomSheetBinding
import de.schnettler.tvtracker.ui.detail.DetailViewModel
import de.schnettler.tvtracker.util.SnapOnScrollListener
import de.schnettler.tvtracker.util.SnapOnScrollListener.Companion.NOTIFY_ON_SCROLL_STATE_IDLE
import javax.inject.Inject

@AndroidEntryPoint
class EpisodeFragment : BottomSheetDialogFragment() {

    @Inject lateinit var assistedFactory: EpisodeViewModel.AssistedFactory
    private val args : EpisodeFragmentArgs by navArgs()
    private lateinit var binding: EpisodeBottomSheetBinding

    private val viewModel: EpisodeViewModel by viewModels {
        EpisodeViewModel.provideFactory(
            assistedFactory, args.show, args.episode
        )
    }

    override fun getTheme(): Int = R.style.Widget_AppTheme_BottomSheet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Binding & ViewModel
        binding = EpisodeBottomSheetBinding.inflate(inflater)

        //Epoxy
        val controller = EpisodeController()
        val recycler = binding.viewpager
        recycler.adapter = controller.adapter
        if (savedInstanceState == null) {
            recycler.post { recycler.scrollToPosition(args.episodeIndex) }
        }

        viewModel.episodeList.observe(viewLifecycleOwner, Observer {
            controller.submitList(it)
        })

        //Snap
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recycler)
        recycler.addOnScrollListener(SnapOnScrollListener(snapHelper, NOTIFY_ON_SCROLL_STATE_IDLE) {position ->
            viewModel.refreshNeighborEpisodes(position)
        })

        //Indicator
        val indicator = binding.recyclerviewPagerIndicator
        indicator.attachToRecyclerView(recycler)

        return binding.root
    }
}