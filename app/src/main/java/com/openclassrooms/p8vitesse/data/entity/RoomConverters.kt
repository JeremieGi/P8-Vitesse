package com.openclassrooms.p8vitesse.data.entity

import androidx.room.TypeConverter
import java.util.Date

/**
 * Converter to help Room to manage Date
 */
class RoomConverters {

    /**
     * Room doesn't know manage type Date
     */

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

}