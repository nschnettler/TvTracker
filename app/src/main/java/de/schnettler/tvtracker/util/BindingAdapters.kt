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
    url?.let {
        bindImageFromUrl(imageView, TvdbService.IMAGE_ENDPOINT + url)
    }
}

fun bindImageFromUrl(imageView: ImageView, fullUrl: String) {
    imageView.clipToOutline = true
    imageView.load(fullUrl) {
        placeholder(R.drawable.loading_animation)
    }
}

@BindingAdapter("status")
fun bindStatus(textView: TextView, status: String?) {
    textView.text = when(status) {
        "returning series" -> "running"
        "in production" -> "in production"
        "planned" -> "planned"
        "canceled" -> "canceled"
        "ended" -> "ended"
        else -> ""
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