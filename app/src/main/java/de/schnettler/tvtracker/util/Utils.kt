package de.schnettler.tvtracker.util

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.airbnb.epoxy.CarouselModelBuilder
import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.google.android.material.appbar.AppBarLayout
import de.schnettler.tvtracker.AuthViewModel
import de.schnettler.tvtracker.ui.discover.DiscoverViewModel
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import kotlin.math.abs


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
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscoverViewModel::class.java)) {
            return DiscoverViewModel(app) as T
        }
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}
class BaseViewModelFactory<T>(val creator: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return creator() as T
    }
}

inline fun <reified T : ViewModel> Fragment.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}

class MaxLinesToggleClickListener(private val collapsedLines: Int) : View.OnClickListener {
    private val transition = ChangeBounds().apply {
        duration = 200
        interpolator = FastOutSlowInInterpolator()
    }

    override fun onClick(view: View) {
        TransitionManager.beginDelayedTransition(view.parent as ViewGroup, transition)
        val textView = view as TextView
        textView.maxLines = if (textView.maxLines > collapsedLines) collapsedLines else Int.MAX_VALUE
    }
}

fun clearLightStatusBar(view: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = view.systemUiVisibility
        flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        view.systemUiVisibility = flags
    }
}

fun setLightStatusBar(view: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = view.systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        view.systemUiVisibility = flags
    }
}

fun setStatusBarColor(resources: Resources, color: Int, window: Window, theme: Resources.Theme) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.statusBarColor = resources.getColor(color, theme)
    }
}


abstract class AppBarStateChangedListener : AppBarLayout.OnOffsetChangedListener {
    private var mCurrentState = State.EXPANDED
    enum class State {EXPANDED, COLLAPSED}

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        when (verticalOffset) {
            0 -> setCurrentStateAndNotify(State.EXPANDED)
            else -> setCurrentStateAndNotify(State.COLLAPSED)
        }
    }

    private fun setCurrentStateAndNotify(state: State) {
        if (mCurrentState != state) {
            onStateChanged(state)
        }
        mCurrentState = state
    }

    abstract fun onStateChanged(state: State)
}

fun isDarkTheme(res: Resources) = ((res.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)

fun getEmoji(genre: String): String = when (genre) {
    "drama" -> "\uD83D\uDE28"
    "fantasy" -> "\uD83E\uDDD9"
    "science-fiction" -> "\uD83D\uDE80️"
    "action" -> "\uD83E\uDD20"
    "adventure" -> "\uD83C\uDFDE️"
    "crime" -> "\uD83D\uDC6E"
    "thriller" -> "\uD83D\uDDE1️"
    "comedy" -> "\uD83E\uDD23"
    "horror" -> "\uD83E\uDDDF"
    "mystery" -> "\uD83D\uDD75️"
    "war" -> "\uD83D\uDCA3"
    "romance" -> "\uD83D\uDC96"
    "soap" -> "\uD83D\uDE0A"
    "superhero" -> "\uD83E\uDDB8"
    "suspense" -> "\uD83D\uDE1F"
    "western" -> "\uD83E\uDD20"
    else -> ""
}


/** For use in the buildModels method of EpoxyController. A shortcut for creating a Carousel model, initializing it, and adding it to the controller.
 *
 */
inline fun EpoxyController.carousel(modelInitializer: CarouselModelBuilder.() -> Unit) {
    CarouselModel_().apply {
        modelInitializer()
    }.addTo(this)
}

/** Add models to a CarouselModel_ by transforming a list of items into EpoxyModels.
 *
 * @param items The items to transform to models
 * @param modelBuilder A function that take an item and returns a new EpoxyModel for that item.
 */
inline fun <T> CarouselModelBuilder.withModelsFrom(
    items: List<T>,
    modelBuilder: (T) -> EpoxyModel<*>
) {
    models(items.map { modelBuilder(it) })
}

fun isoToDate(iso: String): String? {
    val formatter =  DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .appendPattern("XX")
            .toFormatter();
    val date = OffsetDateTime.parse(iso, formatter)
    val formaterNice = DateTimeFormatter.ofPattern("MMMM dd.")

    return date.format(formaterNice)
}

enum class TopListType {
    POPULAR,
    TRENDING,
    ANTICIPATED
}