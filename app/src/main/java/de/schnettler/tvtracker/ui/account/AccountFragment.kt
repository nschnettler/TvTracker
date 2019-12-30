package de.schnettler.tvtracker.ui.account

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import de.schnettler.tvtracker.ui.AuthViewModel
import de.schnettler.tvtracker.data.api.Trakt
import de.schnettler.tvtracker.databinding.AccountFragmentBinding
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.koin.android.viewmodel.ext.android.sharedViewModel


class AccountFragment : Fragment() {

    private val authViewModel: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AccountFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = authViewModel

        authViewModel.startAuthentication.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val authorizeUrl = (Trakt.BASE_URL + "oauth/authorize").toHttpUrlOrNull()
                    ?.newBuilder()
                    ?.addQueryParameter(
                        "client_id",
                        "***TRAKT_CLIENT_ID***"
                    )
                    ?.addQueryParameter("redirect_uri", "de.schnettler.tvtrack://auth")
                    ?.addQueryParameter("response_type", "code")
                    ?.build();

                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(
                    context, Uri.parse(authorizeUrl?.toUri().toString())
                )
                authViewModel.onLoginHandled()
            }

        })
        return binding.root
    }
}