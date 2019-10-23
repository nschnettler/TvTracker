package de.schnettler.tvtracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.schnettler.tvtracker.data.model.PopularDB
import de.schnettler.tvtracker.data.model.ShowDB
import de.schnettler.tvtracker.data.model.TrendingDB

class DataBaseShows {


    @Database(entities = [TrendingDB::class, PopularDB::class,ShowDB::class], version = 1)
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