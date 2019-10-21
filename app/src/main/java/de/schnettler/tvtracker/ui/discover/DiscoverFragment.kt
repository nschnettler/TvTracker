package de.schnettler.tvtracker.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import de.schnettler.tvtracker.databinding.DiscoverFragmentBinding

class DiscoverFragment : Fragment() {

    private lateinit var viewModel: DiscoverViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = DiscoverFragmentBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this).get(DiscoverViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        return binding.root
    }
}
