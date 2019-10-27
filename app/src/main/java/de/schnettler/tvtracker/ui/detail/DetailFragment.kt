package de.schnettler.tvtracker.ui.detail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionInflater
import coil.api.load
import coil.decode.DataSource
import coil.request.Request

import de.schnettler.tvtracker.R
import de.schnettler.tvtracker.databinding.DetailFragmentBinding
import de.schnettler.tvtracker.databinding.DiscoverFragmentBinding
import de.schnettler.tvtracker.util.TMDB_IMAGE_BASE_URL
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.detail_fragment.view.*
import kotlinx.android.synthetic.main.show_view_item.*
import timber.log.Timber

class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private lateinit var binding: DetailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Shared Element Enter
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        binding = DetailFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.appbar.doOnApplyWindowInsets { view, insets, initialState ->
            view.updatePadding(
                top = initialState.paddings.top + insets.systemWindowInsetTop
            )
        }
        val args = DetailFragmentArgs.fromBundle(arguments!!)

        ViewCompat.setTransitionName(binding.showPoster, args.transitionName)
        Timber.i("Transition End ${binding.showPoster.transitionName}")

        val show = args.show
        viewModel = ViewModelProviders.of(this, DetailViewModel.Factory(show)).get(DetailViewModel::class.java)
        binding.viewModel = viewModel

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        }
        return binding.root
    }
}
