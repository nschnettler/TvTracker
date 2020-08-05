package de.schnettler.tvtracker.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.schnettler.tvtracker.data.db.AuthDao
import de.schnettler.tvtracker.data.db.Database
import de.schnettler.tvtracker.data.db.ShowDao

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {
    @Provides
    fun provideDatabase(application: Application): Database = Room.databaseBuilder(
        application,
        Database::class.java, "shows"
    ).fallbackToDestructiveMigration().build()
}

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseDaoModule {
    @Provides
    fun provideShowDao(database: Database): ShowDao = database.showDao

    @Provides
    fun provideAuthDao(database: Database): AuthDao = database.authDao
}