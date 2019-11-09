package de.schnettler.tvtracker.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import de.schnettler.tvtracker.R
import de.schnettler.tvtracker.data.model.Show
import de.schnettler.tvtracker.ui.discover.ShowListAdapter

@BindingAdapter("listShowData")
fun bindRecyclerView(recyclerView: RecyclerView, data: PagedList<Show>?) {
    val adapter = recyclerView.adapter as ShowListAdapter
    adapter.submitList(data)
}

@BindingAdapter(value = ["imageUrl", "imageQuality"], requireAll = true)
fun bindImage(imgView: ImageView, imageUrl: String?, imageQuality: ImageQuality) {
    imageUrl?.let {
        imgView.clipToOutline = true
        imgView.load(TMDB_IMAGE_BASE_URL + imageQuality.quality +  imageUrl) {
            placeholder(R.drawable.loading_animation)
        }
    }
}