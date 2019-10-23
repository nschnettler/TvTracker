package de.schnettler.tvtracker.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import de.schnettler.tvtracker.data.model.*

@Dao
interface TrendingShowsDAO{
    /**
     * Get all Trending Shows. Creates Inner Join of Trending and Shows Table
     */
    @Query("SELECT * FROM table_trending ORDER BY watcher DESC")
    fun getTrending(): LiveData<List<ShowTrendingDB>>

    @Query("SELECT * FROM table_popular INNER JOIN table_show ON id = showId ORDER BY `index` ASC")
    fun getPopular(): LiveData<List<ShowPopularDB>>

    /**
     * Insert a new Shows in table_shows
     */
    @Insert(entity = ShowDB::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertShows(shows: List<ShowDB>)

    @Update(entity = ShowDB::class)
    suspend fun updateShow(show: ShowDB)

    /**
     * Insert new Trendings in table_trending
     */
    @Insert(entity = TrendingDB::class)
    fun insertTrending(trending: List<TrendingDB>)

    @Insert(entity = PopularDB::class)
    fun insertPopular(popular: List<PopularDB>)

    /**
     * Delete all Trending objects
     */
    @Query("DELETE FROM table_trending")
    suspend fun deleteTrendingShows()

    @Query("DELETE FROM table_popular")
    suspend fun deletePopularShows()

    /**
     * Update Trending Shows
     */
    @Transaction
    suspend fun updateTrendingShows(shows: List<ShowTrendingDB>) {
        //Reset Trending Shows
        deleteTrendingShows()
        //Insert new Shows in table_show
        insertShows(shows.map {
            it.show
        })
        //Insert new Trendings in table_trending
        insertTrending(shows.map {
            it.trending
        })
    }

    @Transaction
    suspend fun updatePopularShows(shows: List<ShowPopularDB>) {
        //Reset Trending Shows
        deletePopularShows()
        //Insert new Shows in table_show
        insertShows(shows.map {
            it.show
        })
        //Insert new popular in table_trending
        insertPopular(shows.map {
            it.popular
        })
    }
}

