package de.schnettler.tvtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.airbnb.epoxy.Carousel
import de.schnettler.tvtracker.R
import de.schnettler.tvtracker.databinding.MainActivityBinding
import de.schnettler.tvtracker.util.*
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private lateinit var navController: NavController
    private val viewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.main_activity
        )
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
                    setStatusBarColor(resources,
                        R.color.colorBackgroundTransparent, window, theme)
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