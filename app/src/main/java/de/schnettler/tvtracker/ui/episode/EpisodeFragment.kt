package de.schnettler.tvtracker.ui.episode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.schnettler.tvtracker.databinding.EpisodeFragmentBinding


class EpisodeFragment : Fragment() {

    private lateinit var viewModel: EpisodeViewModel
    private lateinit var binding: EpisodeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EpisodeFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

//        val args = DetailFragmentArgs.fromBundle(arguments!!)
//        val show = args.show
//        viewModel = ViewModelProviders.of(this, DetailViewModel.Factory(show, this.activity!!.application)).get(DetailViewModel::class.java)
//        binding.viewModel = viewModel
//
//        val controller = DetailTypedController()
//        val recycler = binding.recyclerView
//        recycler.adapter = controller.adapter
//
//        viewModel.observeState(this) {
//            controller.setData(it)
//        }

        return binding.root
    }
}
