package de.schnettler.tvtracker.ui.account

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.schnettler.tvtracker.data.api.Trakt
import de.schnettler.tvtracker.databinding.AccountFragmentBinding
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull


class AccountFragment : Fragment() {

    private lateinit var viewModel: AccountViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = AccountFragmentBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.startAuthentication.observe(this, Observer {
            if (it == true) {
                val authorizeUrl = (Trakt.BASE_URL + "oauth/authorize").toHttpUrlOrNull()
                    ?.newBuilder()
                    ?.addQueryParameter("client_id", "***TRAKT_CLIENT_ID***")
                    ?.addQueryParameter("redirect_uri", "de.schnettler.tvtrack://auth")
                    ?.addQueryParameter("response_type", "code")
                    ?.build();

                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(
                    context, Uri.parse(authorizeUrl?.toUri().toString()))
                viewModel.onLoginHandled()
            }

        })
        return binding.root
    }
}
