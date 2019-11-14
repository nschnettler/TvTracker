package de.schnettler.tvtracker.util

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isEmpty
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import de.schnettler.tvtracker.R
import de.schnettler.tvtracker.data.model.Show
import timber.log.Timber


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

@BindingAdapter(value = ["chips", "strokeWidth", "strokeColor", "backgroundColor"], requireAll = false)
fun bindChips(group: ChipGroup, content: List<String>?, strokeWidth: Float, strokeColor: Int, backgroundColor: Int) {
    if (group.isEmpty()) {
        content?.let {
            for (tag in it) {
                val chip = Chip(group.context)
                chip.text= "${getEmoji(tag)} $tag"
                chip.chipStrokeWidth = strokeWidth
                chip.chipStrokeColor = ColorStateList.valueOf(strokeColor)
                chip.chipBackgroundColor = ColorStateList.valueOf(backgroundColor)
                group.addView(chip)
            }
        }
    }
}