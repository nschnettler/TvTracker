package de.schnettler.tvtracker.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import de.schnettler.tvtracker.ui.AuthViewModel
import de.schnettler.tvtracker.ui.MainActivity
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.models.SeasonDomain
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.databinding.DetailFragmentBinding
import de.schnettler.tvtracker.util.*
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class DetailFragment : Fragment() {

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var binding: DetailFragmentBinding
    private val authViewModel: AuthViewModel by viewModel()

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
        detailViewModel = getViewModel { parametersOf(show) }
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
        authViewModel.tvdbLoginStatus.observe(viewLifecycleOwner, Observer {
            if (it) {
                authViewModel.tvdbAuthToken.value?.let {authToken ->
                    detailViewModel.refreshCast(authToken.token)
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

        if(activity is MainActivity){
            val ac = activity as MainActivity
            ac.setSupportActionBar(binding.toolbar)
            ac.supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
