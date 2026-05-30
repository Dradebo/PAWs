package com.sean.pictureaudiowidget.data

import androidx.room.TypeConverter
import com.sean.pictureaudiowidget.media.SortMode

class SortModeConverters {
    @TypeConverter
    fun toSortMode(value: String?): SortMode = value?.let(SortMode::valueOf) ?: SortMode.RANDOM

    @TypeConverter
    fun fromSortMode(sortMode: SortMode): String = sortMode.name
}
