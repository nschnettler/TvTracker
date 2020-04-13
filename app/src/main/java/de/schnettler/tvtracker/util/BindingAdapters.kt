package de.schnettler.tvtracker.util

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.api.clear
import coil.api.load
import de.schnettler.tvtracker.data.api.ImageQuality
import de.schnettler.tvtracker.data.api.TmdbAPI
import de.schnettler.tvtracker.data.api.TvdbAPI
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter(value = ["imageUrl", "imageQuality"], requireAll = true)
fun bindImage(imgView: ImageView, imageUrl: String?, imageQuality: ImageQuality) {
    bindImageFromUrl(imgView, TmdbAPI.IMAGE_ENDPOINT + imageQuality.quality +  imageUrl)
}

@BindingAdapter("tvdbUrl")
fun bindTvdbImage(imageView: ImageView, url: String?) {
    val suffix = if (url.isNullOrBlank()) "/person/actor.jpg" else url
    val prefix = if (suffix.startsWith("/person")) TvdbAPI.IMAGE_ENDPOINT_SHORT else TvdbAPI.IMAGE_ENDPOINT
    bindImageFromUrl(imageView, prefix + suffix)
}

@BindingAdapter("headerUrl")
fun bindBottomSheetHeader(imgView: ImageView, imageUrl: String?) {
    bindImageFromUrl(imgView, TmdbAPI.IMAGE_ENDPOINT + ImageQuality.HIGH.quality +  imageUrl)
}

fun bindImageFromUrl(imageView: ImageView, fullUrl: String?) {
    imageView.clipToOutline = true
    if (fullUrl.isNullOrEmpty()) {
        imageView.clear()
    } else {
        imageView.load(fullUrl) {
            crossfade(true)
        }
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

@BindingAdapter("date")
fun bindDate(view: TextView, date: String?) {
   if (date.isNullOrEmpty()) {
       view.text = ""
   } else {
       SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)?.let {
           view.text = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(it)
       }
   }
}