package de.schnettler.tvtracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.schnettler.tvtracker.data.models.*
import de.schnettler.tvtracker.util.TypeConverter

@TypeConverters(TypeConverter::class)
@Database(
    entities = [TopListEntity::class,
        ShowEntity::class, ShowDetailEntity::class,
        CastEntity::class, AuthTokenEntity::class,
        RelationEntity::class, SeasonEntity::class,
        EpisodeEntity::class, EpisodeDetailEntity::class],
    version = 15
)
abstract class Database : RoomDatabase() {
    abstract val showDao: ShowDao
    abstract val authDao: AuthDao
}