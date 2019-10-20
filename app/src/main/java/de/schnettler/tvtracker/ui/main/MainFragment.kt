package de.schnettler.tvtracker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import de.schnettler.tvtracker.R
import de.schnettler.tvtracker.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = MainFragmentBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        return binding.root
    }
}
