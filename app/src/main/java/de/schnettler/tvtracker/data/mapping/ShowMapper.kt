package de.schnettler.tvtracker.data.mapping

import android.provider.ContactsContract
import de.schnettler.tvtracker.data.show.model.*
import kotlin.math.roundToInt

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

    override fun mapToDomain(input: ShowTrendingDB, index: Int): Show {
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

    override fun mapToDomain(input: ShowPopularDB, index: Int): Show {
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

    override fun mapToDomain(input: AnticipatedShowDB, index: Int): Show {
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

    override fun mapToDomain(input: ShowRelationEntity, index: Int, id: Long): Show {
        return ShowMapper.mapToDomain(input.relatedShow)
    }

}