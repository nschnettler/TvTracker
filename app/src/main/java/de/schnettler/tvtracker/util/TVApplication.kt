package de.schnettler.tvtracker.util

import android.app.Application
import com.facebook.stetho.Stetho
import de.schnettler.tvtracker.AuthViewModel
import de.schnettler.tvtracker.data.api.RetrofitClient
import de.schnettler.tvtracker.data.db.getDatabase
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.data.repository.auth.AuthDataSourceRemote
import de.schnettler.tvtracker.data.repository.auth.AuthRepository
import de.schnettler.tvtracker.data.repository.show.EpisodeRepository
import de.schnettler.tvtracker.data.repository.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.repository.show.IShowRepository
import de.schnettler.tvtracker.data.repository.show.ShowRepository
import de.schnettler.tvtracker.ui.detail.DetailViewModel
import de.schnettler.tvtracker.ui.discover.DiscoverViewModel
import de.schnettler.tvtracker.ui.episode.EpisodeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class TVApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Stetho.initializeWithDefaults(this);
        startKoin{
            androidLogger()
            androidContext(this@TVApplication)
            modules(appModule)
        }
    }
}

val appModule = module {
    //Show Repository
    single<IShowRepository> { ShowRepository(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService, RetrofitClient.imagesNetworkService),
        getDatabase(androidContext()).trendingShowsDao) }
    single { AuthRepository(
        AuthDataSourceRemote(RetrofitClient.tvdbNetworkService, RetrofitClient.showsNetworkService),
        getDatabase(androidContext()).authDao) }
    single { EpisodeRepository(
        ShowDataSourceRemote(RetrofitClient.showsNetworkService, RetrofitClient.tvdbNetworkService, RetrofitClient.imagesNetworkService),
        getDatabase(androidContext()).trendingShowsDao) }

    //ViewModels
    viewModel { DiscoverViewModel(get()) }
    viewModel { (show : ShowDomain) -> DetailViewModel(show, get(), get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { (episode: EpisodeDomain, tmdbId: String) -> EpisodeViewModel(episode, tmdbId, get()) }
}