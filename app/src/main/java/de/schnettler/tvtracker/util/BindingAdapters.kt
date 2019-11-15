package de.schnettler.tvtracker.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.api.load
import de.schnettler.tvtracker.R


@BindingAdapter(value = ["imageUrl", "imageQuality"], requireAll = true)
fun bindImage(imgView: ImageView, imageUrl: String?, imageQuality: ImageQuality) {
    imageUrl?.let {
        imgView.clipToOutline = true
        imgView.load(TMDB_IMAGE_BASE_URL + imageQuality.quality +  imageUrl) {
            placeholder(R.drawable.loading_animation)
        }
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