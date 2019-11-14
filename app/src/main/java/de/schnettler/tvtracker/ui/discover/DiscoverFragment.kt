package de.schnettler.tvtracker.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import de.schnettler.tvtracker.data.model.Show
import de.schnettler.tvtracker.databinding.DiscoverFragmentBinding
import de.schnettler.tvtracker.util.ViewModelFactory
import dev.chrisbanes.insetter.doOnApplyWindowInsets

class DiscoverFragment : Fragment() {

    private lateinit var viewModel: DiscoverViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = DiscoverFragmentBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        viewModel = ViewModelProviders.of(this, ViewModelFactory(application))
            .get(DiscoverViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        val controller = DiscoverController(null)
        val recycler = binding.recyclerView
        recycler.adapter = controller.adapter

        //Recyclerviews
        viewModel.trendingShows.observe(this, Observer{
            controller.trendingShows = it
            controller.requestModelBuild()
        })
        viewModel.popularShows.observe(this, Observer {
            controller.popularShows = it
            controller.requestModelBuild()
        })

        //WindowInsets
        binding.recyclerView.doOnApplyWindowInsets { view, insets, initialState ->
            view.updatePadding(
                top = initialState.paddings.top + insets.systemWindowInsetTop
            )
        }

        //Click Listener Callback
        controller.callbacks = object: DiscoverController.Callbacks {
            override fun onItemClicked(view: View, item: Show) {
                val extras = FragmentNavigatorExtras(
                    view to "showPoster"
                )
                findNavController().navigate(DiscoverFragmentDirections.actionDiscoverToDetailFragment(item, view.transitionName), extras)
            }

        }
        return binding.root
    }
}
