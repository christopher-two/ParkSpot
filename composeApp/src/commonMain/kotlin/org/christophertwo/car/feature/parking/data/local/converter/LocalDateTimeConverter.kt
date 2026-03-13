package org.christophertwo.car.feature.parking.data.local.converter

import androidx.room.TypeConverter

class LocalDateTimeConverter {
    @TypeConverter
    fun fromString(value: String): kotlinx.datetime.LocalDateTime {
        return kotlinx.datetime.LocalDateTime.parse(value)
    }

    @TypeConverter
    fun toString(dateTime: kotlinx.datetime.LocalDateTime): String {
        return dateTime.toString()
    }
}

