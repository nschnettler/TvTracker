package de.schnettler.tvtracker.util

import android.content.Context
import android.widget.Toast

const val TMDB_API_KEY = "***TMDB_API_KEY***"
const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185"

enum class StatusCodes(val code: Int) {
    SUCCESS(200),
    SUCCESS_NEW_SOURCE(201)
}

fun Context.makeToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()