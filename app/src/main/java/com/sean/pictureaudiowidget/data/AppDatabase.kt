package com.sean.pictureaudiowidget.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [WidgetStateEntity::class], version = 2, exportSchema = false)
@TypeConverters(SortModeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun widgetStateDao(): WidgetStateDao
}
