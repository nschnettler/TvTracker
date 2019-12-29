package de.schnettler.tvtracker.data.mapping

import de.schnettler.tvtracker.data.models.*
import de.schnettler.tvtracker.util.TopListType
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

object ListedSHowMapper: IndexedMapper<ShowListResponse, TopListWithShow, ShowDomain> {
    override fun mapToDatabase(input: ShowListResponse, index: Int) = TopListWithShow(
        TopListEntity(
            type = when(input){
                is TrendingResponse -> TopListType.TRENDING.name
                is PopularResponse -> TopListType.POPULAR.name
                is AnticipatedResponse -> TopListType.ANTICIPATED.name
                is RecommendedResponse -> TopListType.RECOMMENDED.name
            },
            index = index,
            showId = input.show.ids.trakt,
            ranking = input.ranking
        ),
        ShowMapper.mapToDatabase(input.show)
    )

    override fun mapToDomain(input: TopListWithShow) = ShowMapper.mapToDomain(input.show)
}


object ShowMapper : Mapper<ShowResponse, ShowEntity, ShowDomain> {
    override fun mapToDomain(input: ShowEntity): ShowDomain {
        return ShowDomain(
            id = input.id,
            tvdbId = input.tvdbId,
            tmdbId = input.tmdbId,
            title = input.title,
            posterUrl = input.posterUrl,
            backdropUrl = input.backdropUrl
        )
    }

    override fun mapToDatabase(input: ShowResponse): ShowEntity {
        return ShowEntity(
            id = input.ids.trakt,
            title = input.title,
            tvdbId = input.ids.tvdb,
            tmdbId = input.ids.tmdb.toString(),
            posterUrl = "",
            backdropUrl = ""
        )
    }
}


object ShowDetailsMapper : Mapper<ShowDetailResponse, ShowDetailEntity, ShowDetailDomain> {
    override fun mapToDatabase(input: ShowDetailResponse): ShowDetailEntity {
        return ShowDetailEntity(
            showId = input.ids.trakt,
            overview = input.overview,
            firstAired = input.firstAired,
            runtime = input.runtime,
            network = input.network,
            trailer = input.trailer,
            status = input.status,
            rating = input.rating.toFloat().times(10).roundToInt().toString(),
            genres = input.genres
        )
    }

    override fun mapToDomain(input: ShowDetailEntity): ShowDetailDomain {
        return ShowDetailDomain(
            showId = input.showId,
            overview = input.overview,
            firstAired = input.firstAired,
            runtime = input.runtime,
            network = input.network,
            trailer = input.trailer,
            status = input.status,
            rating = input.rating,
            genres = input.genres
        )
    }
}

object ShowRelatedMapper : IndexedMapperWithId<ShowResponse, RelationWithShow, ShowDomain> {
    override fun mapToDatabase(
        input: ShowResponse,
        index: Int,
        vararg ids: Long
    ): RelationWithShow {
        return RelationWithShow(
            RelationEntity(
                index = index,
                sourceId = ids[0],
                targetId = input.ids.trakt
            ),
            ShowMapper.mapToDatabase(input)
        )
    }

    override fun mapToDomain(input: RelationWithShow): ShowDomain {
        return ShowMapper.mapToDomain(input.relatedShow)
    }
}

object SeasonSummaryMapper : IndexedMapperWithId<SeasonResponse, SeasonEntity, SeasonDomain> {
    override fun mapToDatabase(input: SeasonResponse, index: Int, vararg ids: Long): SeasonEntity =
        SeasonEntity(
            id = input.ids.trakt,
            rating = input.rating.times(10).roundToLong(),
            firstAired = input.firstAired,
            overview = input.overview,
            title = input.title,
            number = input.number,
            showId = ids[0],
            episodeCount = input.episodeCount
        )

    override fun mapToDomain(input: SeasonEntity) =
        SeasonDomain(
            id = input.id,
            rating = input.rating,
            firstAired = input.firstAired,
            overview = input.overview,
            title = input.title,
            number = input.number,
            episodeCount = input.episodeCount
        )
}

object EpisodeMapper : IndexedMapperWithId<EpisodeResponse, EpisodeEntity, EpisodeDomain> {
    override fun mapToDatabase(
        input: EpisodeResponse,
        index: Int,
        vararg ids: Long
    ): EpisodeEntity {
        val translation = input.translations.find {
            it.language == Locale.getDefault().language
        }
        return EpisodeEntity(
            id = input.ids.trakt,
            showId = ids[0],
            seasonId = ids[1],
            number = input.number,
            title = translation?.title ?: input.title,
            overview = translation?.overview ?: input.overview,
            season = input.season
        )
    }

    override fun mapToDomain(input: EpisodeEntity): EpisodeDomain =
        EpisodeDomain(
            id = input.id,
            showId = input.showId,
            seasonId = input.seasonId,
            number = input.number,
            title = input.title,
            overview = input.overview,
            season = input.season
        )
}

object SeasonWithEpisodeMapper : IndexedMapper<Any, SeasonWithEpisodes, SeasonDomain> {
    override fun mapToDatabase(input: Any, index: Int): SeasonWithEpisodes {
        TODO("not implemented")
    }

    override fun mapToDomain(input: SeasonWithEpisodes): SeasonDomain {
        val season = SeasonSummaryMapper.mapToDomain(input.season)
        season.episodes = input.episodes.map { EpisodeMapper.mapToDomain(it) }
        return season
    }
}

object EpisodeDetailMapper: MapperWithId<EpisodeDetailResponse, EpisodeDetailEntity, EpisodeDetailDomain> {
    override fun mapToDatabase(
        input: EpisodeDetailResponse,
        id: Long
    ) =  EpisodeDetailEntity(
        episodeId = id,
        airDate = input.airDate,
        stillPath = input.stillPath,
        voteAverage = input.voteAverage.times(10).roundToInt()
    )

    override fun mapToDomain(input: EpisodeDetailEntity) = EpisodeDetailDomain(
        episodeId = input.episodeId,
        airDate = input.airDate,
        stillPath = input.stillPath,
        voteAverage = input.voteAverage
    )
}

object EpisodeFullMapper: Mapper<Any, EpisodeWithDetails, EpisodeFullDomain> {
    override fun mapToDatabase(input: Any): EpisodeWithDetails {
        TODO("not implemented")
    }

    override fun mapToDomain(input: EpisodeWithDetails) = EpisodeFullDomain(
            id = input.episode.id,
            seasonId = input.episode.seasonId,
            season = input.episode.season,
            number = input.episode.number,
            title = input.episode.title,
            overview = input.episode.overview,
            airDate = input.details?.airDate ,
            stillPath = input.details?.stillPath,
            voteAverage = input.details?.voteAverage
        )
}