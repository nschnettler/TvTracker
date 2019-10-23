/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.schnettler.tvtracker.util

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import de.schnettler.tvtracker.R
import de.schnettler.tvtracker.data.model.Show
import de.schnettler.tvtracker.ui.discover.ShowListAdapter
import timber.log.Timber


const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185"

@BindingAdapter("listShowData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Show>?) {
    Timber.i("Added ${data?.size} Shows")
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