package de.schnettler.tvtracker

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import de.schnettler.tvtracker.databinding.MainActivityBinding
import android.view.View
import android.view.View.*
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.airbnb.epoxy.Carousel
import de.schnettler.tvtracker.util.*

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
}