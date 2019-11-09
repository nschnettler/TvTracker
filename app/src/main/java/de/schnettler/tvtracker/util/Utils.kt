package de.schnettler.tvtracker.util

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs


const val TMDB_API_KEY = "***TMDB_API_KEY***"
const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
const val SHOW_LIST_PAGE_SIZE = 10
const val SHOW_LIST_PAGE_MAX = 5

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