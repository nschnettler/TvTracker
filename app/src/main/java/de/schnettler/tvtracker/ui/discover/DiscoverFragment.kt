package de.schnettler.tvtracker.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.schnettler.tvtracker.databinding.DiscoverFragmentBinding
import dev.chrisbanes.insetter.Insetter
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
        //viewModel = ViewModelProviders.of(this).get(DiscoverViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.trendingShowsHolder.trendingRecycler.adapter = ShowListAdapter(ShowListAdapter.OnClickListener{
            viewModel.onShowClicked(it)
        })
        binding.popular.trendingRecycler.adapter = ShowListAdapter(ShowListAdapter.OnClickListener{
            viewModel.onShowClicked(it)
        })
        binding.discoverScroll.doOnApplyWindowInsets { view, insets, initialState ->
            view.updatePadding(
                top = initialState.paddings.top + insets.systemWindowInsetTop
            )
        }
        binding.discoverScroll.clipToPadding = false

        return binding.root
    }
}
