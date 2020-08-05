package de.schnettler.tvtracker.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.tvtracker.ui.AuthViewModel
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.models.SeasonDomain
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.databinding.DetailFragmentBinding
import de.schnettler.tvtracker.util.*
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()
    @Inject lateinit var detailViewModelAssistedFactory: DetailViewModel.AssistedFactory
    private val detailViewModel: DetailViewModel by viewModels {
        DetailViewModel.provideFactory(
            detailViewModelAssistedFactory, args.show
        )
    }
    private lateinit var binding: DetailFragmentBinding
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Shared Element Enter
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        //postponeEnterTransition()

        binding = DetailFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val args = DetailFragmentArgs.fromBundle(arguments!!)
        val show = args.show
        binding.viewModel = detailViewModel
        binding.toolbar.title = detailViewModel.show.title

        val controller = DetailController()
        val recycler = binding.recyclerView
        recycler.adapter = controller.adapter

        detailViewModel.observeState(viewLifecycleOwner) {
            controller.setData(it)
            binding.recyclerView.doOnPreDraw {
                //startPostponedEnterTransition()
            }
        }
        detailViewModel.status.observe(viewLifecycleOwner, Observer { status ->
            status?.let {
                detailViewModel.resetStatus()
                Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
            }
        })
        authViewModel.tvdbLoginStatus.observe(viewLifecycleOwner, Observer {
            if (it) {
                authViewModel.tvdbAuthToken.value?.let {authToken ->
                    detailViewModel.onLogin(authToken.token)
                }
            }
        })

        //StatusBar Icon Color
        binding.appbar.addOnOffsetChangedListener(object : AppBarStateChangedListener() {
            override fun onStateChanged(state: State) {
                when(state) {
                    State.COLLAPSED -> {
                        if (!isDarkTheme(resources)) {
                            setLightStatusBar(activity!!.window.decorView)
                        }
                    }
                    State.EXPANDED -> clearLightStatusBar(activity!!.window.decorView)
                }
            }
        })

        //WindowInsets
        binding.recyclerView.doOnApplyWindowInsets { view, insets, initialState ->
            view.updatePadding(
                bottom = initialState.paddings.bottom + insets.systemWindowInsetBottom
            )
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        //Click Listener Callback
        controller.callbacks = object: DetailController.Callbacks {
            override fun onEpisodeClicked(episode: EpisodeDomain) {
                findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToEpisodeFragment(episode, show, detailViewModel.getIndexOfEpisode(episode)))
            }

            override fun onSeasonClicked(season: SeasonDomain, isExpanded: Boolean) {
                detailViewModel.onChangeSeasonExpansion(season, !isExpanded)
            }

            override fun onShowClicked(view: View, item: ShowDomain) {
                val extras = FragmentNavigatorExtras(
                    view to "showPoster"
                )
                findNavController().navigate(DetailFragmentDirections.actionDetailFragmentSelf(item, view.transitionName), extras)
            }

        }
        return binding.root
    }
}
