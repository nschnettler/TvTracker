package de.schnettler.tvtracker.data.mapping

import de.schnettler.tvtracker.data.show.model.*

object TrendingShowMapper: Mapper<TrendingShowRemote, ShowTrendingDB, Show> {

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


object PopularShowMapper: Mapper<ShowRemote, ShowPopularDB, Show> {
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


object ShowMapper: Mapper<ShowRemote, ShowDB, Show> {
    override fun mapToDomain(input: ShowDB, index: Int): Show {
        return Show(
            id = input.id,
            tvdbId = input.tvdbId,
            tmdbId = input.tmdbId,
            title = input.title,
            posterUrl = input.posterUrl,
            backdropUrl = input.backdropUrl,
            index = index
        )
    }

    override fun mapToDatabase(input: ShowRemote, index: Int): ShowDB {
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