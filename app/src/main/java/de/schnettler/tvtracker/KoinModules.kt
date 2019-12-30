package de.schnettler.tvtracker

import androidx.room.Room
import de.schnettler.tvtracker.data.api.*
import de.schnettler.tvtracker.data.db.Database
import de.schnettler.tvtracker.data.models.EpisodeDomain
import de.schnettler.tvtracker.data.models.ShowDomain
import de.schnettler.tvtracker.data.repository.auth.AuthDataSourceRemote
import de.schnettler.tvtracker.data.repository.auth.AuthRepository
import de.schnettler.tvtracker.data.repository.show.EpisodeRepository
import de.schnettler.tvtracker.data.repository.show.IShowRepository
import de.schnettler.tvtracker.data.repository.show.ShowDataSourceRemote
import de.schnettler.tvtracker.data.repository.show.ShowRepository
import de.schnettler.tvtracker.ui.AuthViewModel
import de.schnettler.tvtracker.ui.detail.DetailViewModel
import de.schnettler.tvtracker.ui.discover.DiscoverViewModel
import de.schnettler.tvtracker.ui.episode.EpisodeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

//Database
val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            Database::class.java, "shows"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<Database>().showDao }
    single { get<Database>().authDao }
}

//Repository
val repositoryModule = module {
    single { ShowDataSourceRemote(RetrofitService.traktService, RetrofitService.tvdbService, RetrofitService.tmdbService) }
    single { AuthDataSourceRemote(RetrofitService.tvdbService, RetrofitService.traktService) }
    single<IShowRepository> { ShowRepository(get(), get()) }
    single { AuthRepository(get(), get()) }
    single { EpisodeRepository(get(), get()) }
}

//ViewModel
val viewModelModule = module {
    viewModel { DiscoverViewModel(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { (show: ShowDomain) -> DetailViewModel(show, get(), get()) }
    viewModel { (episode: EpisodeDomain, tmdbId: String) ->
        EpisodeViewModel(
            episode,
            tmdbId,
            get()
        )
    }
}