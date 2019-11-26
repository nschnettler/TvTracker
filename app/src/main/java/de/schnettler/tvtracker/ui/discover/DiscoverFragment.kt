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
import de.schnettler.tvtracker.AuthViewModel
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.databinding.DiscoverFragmentBinding
import de.schnettler.tvtracker.util.ViewModelFactory
import de.schnettler.tvtracker.util.getViewModel
import dev.chrisbanes.insetter.doOnApplyWindowInsets

class DiscoverFragment : Fragment() {

    private lateinit var viewModel: DiscoverViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var controller: DiscoverController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = DiscoverFragmentBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        viewModel = getViewModel {DiscoverViewModel(activity!!.application)}
        authViewModel = getViewModel { AuthViewModel(activity!!.application) }
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        controller = DiscoverController(null)
        controller.onRestoreInstanceState(savedInstanceState)
        val recycler = binding.recyclerView
        recycler.adapter = controller.adapter

        //Recyclerviews
        viewModel.trendingShows.observe(viewLifecycleOwner, Observer{
            controller.trendingShows = it
            controller.requestModelBuild()
        })
        viewModel.popularShows.observe(viewLifecycleOwner, Observer {
            controller.popularShows = it
            controller.requestModelBuild()
        })
        viewModel.anticipatedShows.observe(viewLifecycleOwner, Observer {
            controller.anticipatedShows = it
            controller.requestModelBuild()
        })
        viewModel.recommendedShows.observe(viewLifecycleOwner, Observer {
            controller.recommendedShows = it
            controller.requestModelBuild()
        })

        authViewModel.traktLoginStatus.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.onLoginChanged(it, authViewModel.traktAuthToken.value!!.token)
            }

        })

        //WindowInsets
        binding.recyclerView.doOnApplyWindowInsets { view, insets, initialState ->
            view.updatePadding(
                top = initialState.paddings.top + insets.systemWindowInsetTop
            )
        }

        //Click Listener Callback
        controller.callbacks = object: DiscoverController.Callbacks {
            override fun onItemClicked(view: View, item: ShowDomain) {
                val extras = FragmentNavigatorExtras(
                    view to "showPoster"
                )
                findNavController().navigate(DiscoverFragmentDirections.actionDiscoverToDetailFragment(item, view.transitionName), extras)
            }

        }
        return binding.root
    }


    //Only gets called on Configuration Change (Activity Recreation)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (this::controller.isInitialized) {
            controller.onSaveInstanceState(outState)
        }
    }
}
