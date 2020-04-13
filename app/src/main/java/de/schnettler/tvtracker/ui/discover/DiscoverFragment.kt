package de.schnettler.tvtracker.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.facebook.stetho.common.android.FragmentCompat
import de.schnettler.tvtracker.ui.AuthViewModel
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.databinding.DiscoverFragmentBinding
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import org.koin.android.viewmodel.ext.android.viewModel

class DiscoverFragment : Fragment() {

    private val viewModel: DiscoverViewModel by viewModel()
    private val authViewModel: AuthViewModel by viewModel()
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
