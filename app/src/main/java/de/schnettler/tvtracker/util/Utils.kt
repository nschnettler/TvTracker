package de.schnettler.tvtracker.util

import android.app.Application
import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.schnettler.tvtracker.MainViewModel
import de.schnettler.tvtracker.ui.discover.DiscoverViewModel
import kotlin.math.abs


const val TMDB_API_KEY = "***TMDB_API_KEY***"
const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
const val SHOW_LIST_PAGE_SIZE = 10
const val SHOW_LIST_PAGE_MAX = 5
const val TRAKT_API_BASE_URL = "https://api.trakt.tv/"
const val TRAKT_BASE_URL = "https://trakt.tv/"
const val TMD_BASE_URL = "https://api.themoviedb.org/"
const val TRAKT_CLIENT_ID = "***TRAKT_CLIENT_ID***"
const val TRAKT_REDIRECT_URI = "de.schnettler.tvtrack://auth"
const val RC_AUTH = 100

enum class ImageQuality(val quality: String) {
    LOW("w185"),
    MEDIUM("w300"),
    HIGH("w1280"),
    ORIGINAL("original")
}

enum class StatusCodes(val code: Int) {
    SUCCESS(200),
    SUCCESS_NEW_SOURCE(201)
}

enum class ShowListType() {
    TRENDING,
    POPULAR
}

fun Context.makeToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

class SwipeRefreshLayout(context: Context, attrs: AttributeSet? = null) : SwipeRefreshLayout(context, attrs) {

    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private var previousX = 0F
    private var declined: Boolean = false

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                previousX = event.x
                declined = false
            }

            MotionEvent.ACTION_MOVE -> {
                val eventX = event.x
                val xDiff = abs(eventX - previousX)

                if (declined || xDiff > touchSlop) {
                    declined = true
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }
}

/**
 * Factory for constructing DevByteViewModel with parameter
 */
class ViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscoverViewModel::class.java)) {
            return DiscoverViewModel(app) as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}