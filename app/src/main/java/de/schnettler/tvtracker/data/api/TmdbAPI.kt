package de.schnettler.tvtracker.data.api

import de.schnettler.tvtracker.data.models.EpisodeDetailResponse
import de.schnettler.tvtracker.data.models.PersonImageResponse
import de.schnettler.tvtracker.data.models.ShowImageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbAPI {
    companion object {
        const val ENDPOINT = "https://api.themoviedb.org/"
        const val API_KEY = "***TMDB_API_KEY***"
        const val IMAGE_ENDPOINT = "https://image.tmdb.org/t/p/"
    }

    @GET("/3/tv/{tv_id}")
    suspend fun getShowPoster(
        @Path("tv_id") id: String,
        @Query("api_key") apiKey: String
    ): Response<ShowImageResponse>

    @GET("/3/person/{person_id}")
    suspend fun getPersonImage(
        @Path("person_id") id: String,
        @Query("api_key") apiKey: String
    ): Response<PersonImageResponse>

    @GET("/3/tv/{tv_id}/season/{season_number}/episode/{episode_number}")
    suspend fun getEpisodeDetail(
        @Path("tv_id") showId: String,
        @Path("season_number") seasonNumber: Long,
        @Path("episode_number") episodeNumber: Long,
        @Query("api_key") apiKey: String
    ): Response<EpisodeDetailResponse>
}