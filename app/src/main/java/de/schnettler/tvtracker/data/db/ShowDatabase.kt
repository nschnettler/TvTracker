package de.schnettler.tvtracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.schnettler.tvtracker.data.models.AuthTokenEntity
import de.schnettler.tvtracker.data.models.*
import de.schnettler.tvtracker.util.TypeConverter

class DataBaseShows {

    @TypeConverters(TypeConverter::class)
    @Database(entities = [TrendingEntity::class,
        PopularEntity::class, ShowEntity::class,
        ShowDetailEntity::class, CastEntity::class,
        AuthTokenEntity::class, RelationEntity::class,
        AnticipatedEntity::class, SeasonEntity::class,
        EpisodeEntity::class,
        EpisodeDetailEntity::class],
        version = 14)
    abstract class ShowsDatabase : RoomDatabase() {
        //abstract val showDao: ShowDao
        abstract val trendingShowsDao: ShowDao
        abstract val authDao: AuthDao
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