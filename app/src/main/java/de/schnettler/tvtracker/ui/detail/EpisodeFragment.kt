package de.schnettler.tvtracker.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.schnettler.tvtracker.R
import de.schnettler.tvtracker.databinding.EpisodeBottomSheetBinding
import de.schnettler.tvtracker.util.getViewModel
import timber.log.Timber

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
        binding.lifecycleOwner = this
        viewModel = getViewModel { EpisodeViewModel(args.episode, args.show, activity!!.application) }
        binding.viewModel = viewModel

        viewModel.episodeDetails.observe(viewLifecycleOwner, Observer {
            Timber.i(it?.stillPath)
        })

        return binding.root
    }
}