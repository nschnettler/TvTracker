package de.schnettler.tvtracker.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.airbnb.epoxy.Carousel
import de.schnettler.tvtracker.MainActivity
import de.schnettler.tvtracker.data.show.model.Show
import de.schnettler.tvtracker.databinding.DetailFragmentBinding
import de.schnettler.tvtracker.ui.discover.DiscoverController
import de.schnettler.tvtracker.ui.discover.DiscoverFragmentDirections
import de.schnettler.tvtracker.util.*
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

        val args = DetailFragmentArgs.fromBundle(arguments!!)
        val show = args.show
        viewModel = ViewModelProviders.of(this, DetailViewModel.Factory(show, this.activity!!.application)).get(DetailViewModel::class.java)
        binding.viewModel = viewModel

        val controller = DetailTypedController()
        val recycler = binding.recyclerView
        recycler.adapter = controller.adapter
        Carousel.setDefaultGlobalSnapHelperFactory(null)

        viewModel.observeState(this) {
            controller.setData(it)
        }

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

        if(activity is MainActivity){
            val ac = activity as MainActivity
            ac.setSupportActionBar(binding.toolbar)
            ac.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        //Click Listener Callback
        controller.callbacks = object: DetailTypedController.Callbacks {
            override fun onItemClicked(view: View, item: Show) {
                val extras = FragmentNavigatorExtras(
                    view to "showPoster"
                )
                findNavController().navigate(DetailFragmentDirections.actionDetailFragmentSelf(item, view.transitionName), extras)
            }

        }
        return binding.root
    }
}
