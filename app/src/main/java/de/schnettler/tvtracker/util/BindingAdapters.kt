package de.schnettler.tvtracker.util

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.api.load
import de.schnettler.tvtracker.data.api.ImageQuality
import de.schnettler.tvtracker.data.api.TMDb
import de.schnettler.tvtracker.data.api.TVDB
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter(value = ["imageUrl", "imageQuality"], requireAll = true)
fun bindImage(imgView: ImageView, imageUrl: String?, imageQuality: ImageQuality) {
    imageUrl?.let {
        bindImageFromUrl(imgView, TMDb.IMAGE_ENDPOINT + imageQuality.quality +  imageUrl)
    }
}

@BindingAdapter("tvdbUrl")
fun bindTvdbImage(imageView: ImageView, url: String?) {
    val suffix = if (url.isNullOrBlank()) "/person/actor.jpg" else url
    val prefix = if (suffix.startsWith("/person")) TVDB.IMAGE_ENDPOINT_SHORT else TVDB.IMAGE_ENDPOINT
    bindImageFromUrl(imageView, prefix + suffix)
}

@BindingAdapter("headerUrl")
fun bindBottomSheetHeader(imgView: ImageView, imageUrl: String?) {
    imgView.outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(0, 0, view!!.width, (view.height+ 54F).toInt(), 54F)
        }
    }
    imageUrl?.let {
        bindImageFromUrl(imgView, TMDb.IMAGE_ENDPOINT + ImageQuality.HIGH.quality +  imageUrl)
    }
}

fun bindImageFromUrl(imageView: ImageView, fullUrl: String) {
    imageView.clipToOutline = true
    imageView.load(fullUrl) {
        crossfade(true)
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
    date?.let {dateString ->
        SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateString)?.let {
            view.text = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(it)
        }
    }
}