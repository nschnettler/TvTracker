package de.schnettler.tvtracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.schnettler.tvtracker.data.auth.model.AuthTokenDB
import de.schnettler.tvtracker.data.show.model.*
import de.schnettler.tvtracker.data.show.model.cast.CastEntry
import de.schnettler.tvtracker.data.show.model.episode.EpisodeEntity
import de.schnettler.tvtracker.data.show.model.season.SeasonEntity
import de.schnettler.tvtracker.util.TypeConverter

class DataBaseShows {

    @TypeConverters(TypeConverter::class)
    @Database(entities = [TrendingDB::class, PopularDB::class, ShowDB::class, ShowDetailsDB::class, CastEntry::class, AuthTokenDB::class, RelationEntity::class, AnticipatedDB::class, SeasonEntity::class, EpisodeEntity::class], version = 9)
    abstract class ShowsDatabase : RoomDatabase() {
        //abstract val showDao: ShowDao
        abstract val trendingShowsDao: TrendingShowsDAO
    }
}
private lateinit var INSTANCE: DataBaseShows.ShowsDatabase

fun getDatabase(context: Context): DataBaseShows.ShowsDatabase {
    synchronized(DataBaseShows.ShowsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                DataBaseShows.ShowsDatabase::class.java, "shows").fallbackToDestructiveMigration().build()
        }
    }
    return INSTANCE
}