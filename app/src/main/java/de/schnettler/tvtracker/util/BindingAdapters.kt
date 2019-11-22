package de.schnettler.tvtracker.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.api.load
import de.schnettler.tvtracker.R
import de.schnettler.tvtracker.data.api.tvdb.TvdbService


@BindingAdapter(value = ["imageUrl", "imageQuality"], requireAll = true)
fun bindImage(imgView: ImageView, imageUrl: String?, imageQuality: ImageQuality) {
    imageUrl?.let {
        bindImageFromUrl(imgView, TMDB_IMAGE_BASE_URL + imageQuality.quality +  imageUrl)
    }
}

@BindingAdapter("tvdbUrl")
fun bindTvdbImage(imageView: ImageView, url: String?) {
    val suffix = if (url.isNullOrBlank()) "/person/actor.jpg" else url
    val prefix = if (suffix.startsWith("/person")) TvdbService.IMAGE_ENDPOINT_SHORT else TvdbService.IMAGE_ENDPOINT
    bindImageFromUrl(imageView, prefix + suffix)
}

fun bindImageFromUrl(imageView: ImageView, fullUrl: String) {
    imageView.clipToOutline = true
    imageView.load(fullUrl) {
        placeholder(R.drawable.loading_animation)
    }
}

@BindingAdapter("maxLinesToggle")
fun maxLinesClickListener(view: TextView, oldCollapsedMaxLines: Int, newCollapsedMaxLines: Int) {
    if (oldCollapsedMaxLines != newCollapsedMaxLines) {
        // Default to collapsed
        view.maxLines = newCollapsedMaxLines
        // Now set click listener
        view.setOnClickListener(MaxLinesToggleClickListener(newCollapsedMaxLines))
    }
}