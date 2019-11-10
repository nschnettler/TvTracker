package de.schnettler.tvtracker.util

class TypeConverter {
    @androidx.room.TypeConverter
    fun fromString(value: String)= value.split(",").map { it }

    @androidx.room.TypeConverter
    fun toString(list: List<String>) = list.joinToString(",")
}