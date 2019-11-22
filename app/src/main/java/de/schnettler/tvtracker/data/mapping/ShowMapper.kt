package de.schnettler.tvtracker.data.mapping

import de.schnettler.tvtracker.data.show.model.*
import de.schnettler.tvtracker.data.show.model.season.SeasonDomain
import de.schnettler.tvtracker.data.show.model.season.SeasonEntity
import de.schnettler.tvtracker.data.show.model.season.SeasonResponse
import kotlin.math.roundToInt
import kotlin.math.roundToLong

object TrendingShowMapper : IndexedMapper<TrendingShowRemote, ShowTrendingDB, Show> {

    override fun mapToDatabase(input: TrendingShowRemote, index: Int): ShowTrendingDB {
        return ShowTrendingDB(
            TrendingDB(
                index = index,
                showId = input.show.ids.trakt,
                watcher = input.watchers
            ),
            ShowMapper.mapToDatabase(input.show)
        )
    }

    override fun mapToDomain(input: ShowTrendingDB): Show {
        return ShowMapper.mapToDomain(input.show)
    }
}


object PopularShowMapper : IndexedMapper<ShowRemote, ShowPopularDB, Show> {
    override fun mapToDatabase(input: ShowRemote, index: Int): ShowPopularDB {
        return ShowPopularDB(
            PopularDB(
                index = index,
                showId = input.ids.trakt
            ),
            ShowMapper.mapToDatabase(input)
        )
    }

    override fun mapToDomain(input: ShowPopularDB): Show {
        return ShowMapper.mapToDomain(input.show)
    }
}

object AnticipatedShowMapper : IndexedMapper<AnticipatedShowRemote, AnticipatedShowDB, Show> {
    override fun mapToDatabase(input: AnticipatedShowRemote, index: Int): AnticipatedShowDB {
        return AnticipatedShowDB(
            AnticipatedDB(
                index = index,
                showId = input.show.ids.trakt,
                lists = input.listCount
            ),
            ShowMapper.mapToDatabase(input.show)

        )
    }

    override fun mapToDomain(input: AnticipatedShowDB): Show {
        return ShowMapper.mapToDomain(input.show)
    }
}


object ShowMapper : Mapper<ShowRemote, ShowDB, Show> {
    override fun mapToDomain(input: ShowDB): Show {
        return Show(
            id = input.id,
            tvdbId = input.tvdbId,
            tmdbId = input.tmdbId,
            title = input.title,
            posterUrl = input.posterUrl,
            backdropUrl = input.backdropUrl
        )
    }

    override fun mapToDatabase(input: ShowRemote): ShowDB {
        return ShowDB(
            id = input.ids.trakt,
            title = input.title,
            tvdbId = input.ids.tvdb,
            tmdbId = input.ids.tmdb.toString(),
            posterUrl = "",
            backdropUrl = ""
        )
    }
}


object ShowDetailsMapper : Mapper<ShowDetailsRemote, ShowDetailsDB, ShowDetails> {
    override fun mapToDatabase(input: ShowDetailsRemote): ShowDetailsDB {
        return ShowDetailsDB(
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

    override fun mapToDomain(input: ShowDetailsDB): ShowDetails {
        return ShowDetails(
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

object ShowRelatedMapper : IndexedMapperWithId<ShowRemote, ShowRelationEntity, Show> {
    override fun mapToDatabase(input: ShowRemote, index: Int, id: Long): ShowRelationEntity {
        return ShowRelationEntity(
            RelationEntity(
                index = index,
                sourceId = id,
                targetId = input.ids.trakt
            ),
            ShowMapper.mapToDatabase(input)
        )
    }

    override fun mapToDomain(input: ShowRelationEntity): Show {
        return ShowMapper.mapToDomain(input.relatedShow)
    }
}

object SeasonSummaryMapper: IndexedMapperWithId<SeasonResponse, SeasonEntity, SeasonDomain> {
    override fun mapToDatabase(input: SeasonResponse, index: Int, id: Long)= SeasonEntity(
        id = input.ids.trakt,
        rating = input.rating.times(10).roundToLong(),
        firstAired = input.firstAired,
        overview = input.overview,
        title = input.title,
        number = input.number,
        showId = id,
        episodeCount = input.episodeCount
    )

    override fun mapToDomain(input: SeasonEntity)= SeasonDomain(
        id = input.id,
        rating = input.rating,
        firstAired = input.firstAired,
        overview = input.overview,
        title = input.title,
        number = input.number,
        episodeCount = input.episodeCount
    )
}