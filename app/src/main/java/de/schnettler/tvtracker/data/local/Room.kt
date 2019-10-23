package de.schnettler.tvtracker.data.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import de.schnettler.tvtracker.data.model.ShowDatabase

@Dao
interface ShowDao {
    @Query("SELECT * FROM table_shows_trending")
    fun getTrendingShows(): LiveData<List<ShowDatabase>>

    @Query("DELETE FROM table_shows_trending ")
    suspend fun deleteTrendingShows()

    @Insert
    suspend fun insertTrendingShows(shows: List<ShowDatabase>)

    @Transaction
    suspend fun updateTrendingShows(shows: List<ShowDatabase>) {
        deleteTrendingShows()
        insertTrendingShows(shows)
    }

    @Update
    suspend fun updateShow(show: ShowDatabase)
}

@Database(entities = [ShowDatabase::class], version = 1)
abstract class ShowsDatabase : RoomDatabase() {
    abstract val showDao: ShowDao
}

private lateinit var INSTANCE: ShowsDatabase

fun getDatabase(context: Context): ShowsDatabase {
    synchronized(ShowsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                ShowsDatabase::class.java, "shows").build()
        }
    }
    return INSTANCE
}