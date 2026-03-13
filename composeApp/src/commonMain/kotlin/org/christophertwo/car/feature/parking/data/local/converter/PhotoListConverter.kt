package org.christophertwo.car.feature.parking.data.local.converter

import androidx.room.TypeConverter

class PhotoListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return if (value.isBlank()) emptyList()
        else value.split("|")
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return list.joinToString("|")
    }
}

