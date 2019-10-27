package de.schnettler.tvtracker.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import de.schnettler.tvtracker.databinding.DiscoverFragmentBinding
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import timber.log.Timber

class DiscoverFragment : Fragment() {

    private lateinit var viewModel: DiscoverViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = DiscoverFragmentBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        viewModel = ViewModelProviders.of(this, DiscoverViewModel.Factory(application))
            .get(DiscoverViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.trendingShowsHolder.trendingRecycler.adapter = ShowListAdapter(ShowListAdapter.OnClickListener{ show, view ->
            Timber.i("Transition Start ${view.transitionName}")
            val extras = FragmentNavigatorExtras(
                 view to view.transitionName
            )
            findNavController().navigate(DiscoverFragmentDirections.actionDiscoverToDetailFragment(show, view.transitionName), extras)
        }, "trending")

        binding.popular.trendingRecycler.adapter = ShowListAdapter(ShowListAdapter.OnClickListener{show, view ->
            Timber.i("Transition Start ${view.transitionName}")
            val extras = FragmentNavigatorExtras(
                view to view.transitionName
            )
            findNavController().navigate(DiscoverFragmentDirections.actionDiscoverToDetailFragment(show, view.transitionName), extras)
        }, "popular")
        binding.discoverScroll.doOnApplyWindowInsets { view, insets, initialState ->
            view.updatePadding(
                top = initialState.paddings.top + insets.systemWindowInsetTop
            )
        }
        binding.discoverScroll.clipToPadding = false

        return binding.root
    }
}
