package de.schnettler.tvtracker.util

import android.content.Context
import android.widget.Toast

enum class StatusCodes(val code: Int) {
    SUCCESS(200),
    SUCCESS_NEW_SOURCE(201)
}

fun Context.makeToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()