package de.schnettler.tvtracker.ui.detail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController

import de.schnettler.tvtracker.R
import de.schnettler.tvtracker.databinding.DetailFragmentBinding
import de.schnettler.tvtracker.databinding.DiscoverFragmentBinding
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.detail_fragment.view.*

class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DetailFragmentBinding.inflate(inflater)
        val toolbar = binding.toolbar
        binding.setLifecycleOwner(this)
        binding.appbar.doOnApplyWindowInsets { view, insets, initialState ->
            view.updatePadding(
                top = initialState.paddings.top + insets.systemWindowInsetTop
            )

        }

        if(activity is AppCompatActivity){
            //toolbar?.title = "Test"
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            //(activity as AppCompatActivity).supportActionBar?.title ="test"
        }

        val show = DetailFragmentArgs.fromBundle(arguments!!).show
        binding.viewModel = ViewModelProviders.of(this, DetailViewModel.Factory(show)).get(DetailViewModel::class.java)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
