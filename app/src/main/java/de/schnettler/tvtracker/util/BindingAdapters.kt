package de.schnettler.tvtracker.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import de.schnettler.tvtracker.R
import de.schnettler.tvtracker.data.model.Show
import de.schnettler.tvtracker.ui.discover.ShowListAdapter

@BindingAdapter("listShowData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Show>?) {
    val adapter = recyclerView.adapter as ShowListAdapter
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        imgView.clipToOutline = true
        imgView.load(TMDB_IMAGE_BASE_URL + imgUrl) {
            placeholder(R.drawable.loading_animation)
        }
    }
}