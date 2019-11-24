package de.schnettler.tvtracker

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.airbnb.epoxy.Carousel
import de.schnettler.tvtracker.databinding.MainActivityBinding
import de.schnettler.tvtracker.ui.detail.DetailFragment
import de.schnettler.tvtracker.util.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    lateinit var navController: NavController
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        viewModel = ViewModelProviders.of(this, ViewModelFactory(application))
            .get(MainViewModel::class.java)
        binding.root.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_STABLE
        navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.detailFragment -> {
                    binding.bottomNavigation.visibility = View.GONE
                    clearLightStatusBar(window.decorView)
                    setStatusBarColor(resources, android.R.color.transparent, window, theme)
                }
                R.id.episodeFragment -> binding.bottomNavigation.visibility = View.GONE
                else -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    if (!isDarkTheme(resources)) {
                        setLightStatusBar(window.decorView)
                    }
                    setStatusBarColor(resources, R.color.colorBackgroundTransparent, window, theme)
                }
            }
        }

        Carousel.setDefaultGlobalSnapHelperFactory(null)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.getQueryParameter("code")?.let {
            viewModel.onAuthResponse(it)
        }
    }

    override fun onBackPressed() {
        val fragment = getCurrentFragment()
        if (fragment is DetailFragment) {
            if (!fragment.handleBottomSheet()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val fragment = getCurrentFragment()
        if (fragment is DetailFragment && ev?.action == MotionEvent.ACTION_DOWN) {
            if (fragment.interceptTouchEvent(ev)) {
                return true
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun getCurrentFragment(): Fragment? {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        return navHost?.childFragmentManager?.primaryNavigationFragment
    }
}