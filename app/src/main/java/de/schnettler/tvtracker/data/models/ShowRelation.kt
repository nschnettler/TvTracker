package de.schnettler.tvtracker.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity(tableName = "table_relations", primaryKeys = ["sourceId", "index"])
data class RelationEntity(
    val sourceId: Long,
    val index: Int,
    val targetId: Long
)

class RelationWithShow(
    @Embedded val relation: RelationEntity,
    @Relation(
        parentColumn = "targetId",
        entityColumn = "id"
    )
    val relatedShow: ShowEntity
)