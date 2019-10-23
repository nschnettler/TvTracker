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

package de.schnettler.tvtracker.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

data class TrendingShowRemote(
        val watchers: Long,
        val show: ShowRemote
)

data class ShowRemote(
        val title: String,
        val year: Long,
        val ids: ShowIdRemote)

data class ShowIdRemote(
        val trakt: Long,
        val slug: String,
        val tvdb: Long,
        val imdb: String,
        val tmdb: Long
)

data class ShowImagesRemote(
        val poster_path: String,
        val backdrop_path: String
)

fun List<TrendingShowRemote>.asShowModel(): List<Show>? {
        return map {
                Show (
                        title = it.show.title,
                        tmdbId = it.show.ids.tmdb,
                        traktId = it.show.ids.trakt,
                        year = it.show.year,
                        watchers = it.watchers
                )
        }
}

fun List<TrendingShowRemote>.asShowDatabase(): List<ShowDatabase>? {
        return map {
                ShowDatabase(
                        title = it.show.title,
                        tmdbId = it.show.ids.tmdb,
                        traktId = it.show.ids.trakt,
                        year = it.show.year,
                        watchers = it.watchers,
                        posterUrl = ""
                )
        }
}