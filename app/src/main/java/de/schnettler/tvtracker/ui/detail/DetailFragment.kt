package de.schnettler.tvtracker.ui.detail

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
import androidx.transition.TransitionInflater
import de.schnettler.tvtracker.AuthViewModel
import de.schnettler.tvtracker.MainActivity
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.models.SeasonDomain
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.databinding.DetailFragmentBinding
import de.schnettler.tvtracker.util.*
import dev.chrisbanes.insetter.doOnApplyWindowInsets


class DetailFragment : Fragment() {

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: DetailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Shared Element Enter
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        binding = DetailFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val args = DetailFragmentArgs.fromBundle(arguments!!)
        val show = args.show
        detailViewModel = ViewModelProviders.of(this, DetailViewModel.Factory(show, this.activity!!.application)).get(DetailViewModel::class.java)
        authViewModel = getViewModel {AuthViewModel(activity!!.application)}
        binding.viewModel = detailViewModel

        val controller = DetailController()
        val recycler = binding.recyclerView
        recycler.adapter = controller.adapter

        detailViewModel.observeState(viewLifecycleOwner) {
            controller.setData(it)
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
            override fun onStateChanged(state: AppBarStateChangedListener.State) {
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
                findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToEpisodeFragment(episode, show))
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
