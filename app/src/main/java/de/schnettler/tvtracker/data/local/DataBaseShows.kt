package de.schnettler.tvtracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.schnettler.tvtracker.data.model.*
import de.schnettler.tvtracker.util.TypeConverter

class DataBaseShows {

    @TypeConverters(TypeConverter::class)
    @Database(entities = [TrendingDB::class, PopularDB::class,ShowDB::class, ShowDetailsDB::class, PersonDB::class, CastDB::class], version = 2)
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
                DataBaseShows.ShowsDatabase::class.java, "shows").build()
        }
    }
    return INSTANCE
}