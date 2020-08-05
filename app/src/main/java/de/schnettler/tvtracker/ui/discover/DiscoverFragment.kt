package de.schnettler.tvtracker.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.databinding.DiscoverFragmentBinding
import de.schnettler.tvtracker.ui.AuthViewModel
import dev.chrisbanes.insetter.doOnApplyWindowInsets

@AndroidEntryPoint
class DiscoverFragment : Fragment() {

    private val viewModel: DiscoverViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var controller: DiscoverController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = DiscoverFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        controller = DiscoverController()
        val recycler = binding.recyclerView
        recycler.adapter = controller.adapter

        viewModel.observeState(viewLifecycleOwner) {
            controller.setData(it)
        }

        authViewModel.traktLoginStatus.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.onLogin(authViewModel.traktAuthToken.value?.token)
            }

        })

        viewModel.status.observe(viewLifecycleOwner, Observer { status ->
            status?.let {
                Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
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
