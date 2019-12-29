package de.schnettler.tvtracker.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.schnettler.tvtracker.R
import de.schnettler.tvtracker.databinding.EpisodeBottomSheetBinding
import de.schnettler.tvtracker.util.SnapOnScrollListener
import de.schnettler.tvtracker.util.SnapOnScrollListener.Companion.NOTIFY_ON_SCROLL_STATE_IDLE
import de.schnettler.tvtracker.util.getViewModel


class EpisodeFragment : BottomSheetDialogFragment() {

    private lateinit var binding: EpisodeBottomSheetBinding
    private lateinit var viewModel: EpisodeViewModel

    override fun getTheme(): Int = R.style.Widget_AppTheme_BottomSheet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Args
        val args = EpisodeFragmentArgs.fromBundle(arguments!!)

        //Binding & ViewModel
        binding = EpisodeBottomSheetBinding.inflate(inflater)
        viewModel = getViewModel { EpisodeViewModel(args.episode, args.show.tmdbId, activity!!.application) }

        //Epoxy
        val controller = EpisodeController()
        val recycler = binding.viewpager
        recycler.adapter = controller.adapter
        if (savedInstanceState == null) {
            recycler.post { recycler.scrollToPosition(args.episode.number.toInt() - 1) }
        }

        viewModel.episodeList.observe(viewLifecycleOwner, Observer {
            controller.submitList(it)
        })

        //Snap
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recycler)
        recycler.addOnScrollListener(SnapOnScrollListener(snapHelper, NOTIFY_ON_SCROLL_STATE_IDLE) {position ->
            viewModel.refreshDetails(position)
        })

        //Indicator
        val indicator = binding.recyclerviewPagerIndicator
        indicator.attachToRecyclerView(recycler)

        return binding.root
    }
}