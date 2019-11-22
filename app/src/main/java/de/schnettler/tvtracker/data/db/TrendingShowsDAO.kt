package de.schnettler.tvtracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import de.schnettler.tvtracker.data.auth.model.AuthTokenDB
import de.schnettler.tvtracker.data.show.model.*
import de.schnettler.tvtracker.data.show.model.cast.CastEntry
import de.schnettler.tvtracker.data.show.model.episode.EpisodeEntity
import de.schnettler.tvtracker.data.show.model.season.SeasonEntity
import de.schnettler.tvtracker.data.show.model.season.SeasonWithEpisodes

@Dao
interface TrendingShowsDAO{
    /**
     * Get all Trending Shows. Creates Inner Join of Trending and Shows Table
     */
    @Query("SELECT * FROM table_trending ORDER BY `index` ASC")
    fun getTrending(): LiveData<List<ShowTrendingDB>?>

    @Query("SELECT * FROM table_popular ORDER BY `index` ASC")
    fun getPopular(): LiveData<List<ShowPopularDB>?>

    @Query("SELECT * FROM table_anticipated ORDER BY `index` ASC")
    fun getAnticipated(): LiveData<List<AnticipatedShowDB>?>

    @Query("SELECT * FROM table_show_details WHERE showId = :id")
    fun getShowDetails(id: Long): LiveData<ShowDetailsDB?>

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
    fun insertAnticipated(popular: List<AnticipatedDB>)

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

    @Transaction
    suspend fun insertAnticipatedShows(shows: List<AnticipatedShowDB>) {
        insertShows(shows.map { it.show })
        insertAnticipated(shows.map { it.anticipated })
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCast(castEntry: List<CastEntry>)

    @Query("SELECT * FROM table_cast WHERE showId = :id ORDER BY id ASC LIMIT 10")
    fun getCast(id: Long): LiveData<List<CastEntry>?>

    /*
     * Auth
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAuthToken(authTokenDB: AuthTokenDB)

    @Query("SELECT * FROM table_auth WHERE tokenName = :type")
    fun getAuthToken(type: String): LiveData<AuthTokenDB?>


    /*
     * Related Shows
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelations(relations: List<RelationEntity>)

    @Transaction
    suspend fun insertShowRelations(showRelations: List<ShowRelationEntity>) {
        insertShows(showRelations.map { it.relatedShow })
        insertRelations(showRelations.map { it.relation })
    }

    @Query("SELECT * FROM table_relations WHERE sourceId = :showId ORDER BY `index` ASC")
    fun getShowRelations(showId: Long): LiveData<List<ShowRelationEntity>?>


    /*
     * Season & Episodes Summary
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeasons(seasons: List<SeasonEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<EpisodeEntity>)

    /*
     * Season with Episodes
     */
    @Transaction
    @Query("SELECT * FROM table_seasons WHERE showId = :showId ORDER BY number ASC")
    fun getSeasonsWithEpisodes(showId: Long): LiveData<List<SeasonWithEpisodes>>

    @Transaction
    suspend fun insertSeasonWithEpisodes(seasons: List<SeasonWithEpisodes>) {
        //Insert Season
        insertSeasons(seasons.map { it.season })
        //Insert Episodes
        seasons.forEach {
            insertEpisodes(it.episodes)
        }
    }
}

