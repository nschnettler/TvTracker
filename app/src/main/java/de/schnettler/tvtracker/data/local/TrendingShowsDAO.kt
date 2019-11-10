package de.schnettler.tvtracker.data.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import de.schnettler.tvtracker.data.model.*

@Dao
interface TrendingShowsDAO{
    /**
     * Get all Trending Shows. Creates Inner Join of Trending and Shows Table
     */
    @Query("SELECT * FROM table_trending ORDER BY `index` ASC")
    fun getTrending(): DataSource.Factory<Int, ShowTrendingDB>

    @Query("SELECT * FROM table_popular ORDER BY `index` ASC")
    fun getPopular(): DataSource.Factory<Int, ShowPopularDB>

    @Query("SELECT * FROM table_show_details WHERE showId = :id")
    fun getShowDetails(id: Long): LiveData<ShowDetailsDB>

    /**
     * Insert a new Shows in table_shows
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertShows(shows: List<ShowDB>)

    @Update
    suspend fun updateShow(show: ShowDB)

    /**
     * Insert new Trendings in table_trending
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrending(trending: List<TrendingDB>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPopular(popular: List<PopularDB>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShowDetails(showDetails: ShowDetailsDB)

    @Transaction
    suspend fun insertTrendingShows(shows: List<ShowTrendingDB>) {
        insertShows(shows.map { it.show })
        insertTrending(shows.map { it.trending })
    }

    @Transaction
    suspend fun insertPopularShows(shows: List<ShowPopularDB>) {
        insertShows(shows.map { it.show })
        insertPopular(shows.map { it.popular })
    }
}

