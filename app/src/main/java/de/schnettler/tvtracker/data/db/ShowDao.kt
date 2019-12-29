package de.schnettler.tvtracker.data.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import de.schnettler.tvtracker.data.models.*

@Dao
interface ShowDao {
    /**
     * Top Lists for Discover Tab
     */
    @Transaction
    @Query("SELECT * FROM table_discover WHERE type = :type ORDER BY `index` ASC")
    fun getTopList(type: String): LiveData<List<TopListWithShow>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopList(shows: List<TopListEntity>)


    /*
     * Show Details
     */
    @Query("SELECT * FROM table_show_details WHERE showId = :id")
    fun getShowDetails(id: Long): LiveData<ShowDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShowDetails(showDetails: ShowDetailEntity)


    /*
     * Show
     */
    @Query("SELECT * FROM table_show WHERE id = :id")
    suspend fun getShow(id: Long): ShowEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertShows(shows: List<ShowEntity>)

    @Update
    suspend fun updateShow(show: ShowEntity)


    /*
     Cast
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCast(castEntry: List<CastEntity>)

    @Query("SELECT * FROM table_cast WHERE showId = :id ORDER BY id ASC LIMIT 10")
    fun getCast(id: Long): LiveData<List<CastEntity>?>


    /*
     * Related Shows
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelations(relations: List<RelationEntity>)

    @Transaction
    suspend fun insertShowRelations(showRelations: List<RelationWithShow>) {
        insertShows(showRelations.map { it.relatedShow })
        insertRelations(showRelations.map { it.relation })
    }

    @Transaction
    @Query("SELECT * FROM table_relations WHERE sourceId = :showId ORDER BY `index` ASC")
    fun getShowRelations(showId: Long): LiveData<List<RelationWithShow>?>


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

    /*
     * Episode Details
     *
     */
    @Transaction
    @Query("SELECT * FROM table_episode_details WHERE episodeId = :episodeId")
    fun getEpisodeDetails(episodeId: Long): LiveData<EpisodeDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodeDetail(detail: EpisodeDetailEntity)

    @Transaction
    @Query("SELECT * FROM table_episode WHERE showId = :showId ORDER BY season ASC")
    fun getEpisodes(showId: Long): DataSource.Factory<Int, EpisodeWithDetails>
}