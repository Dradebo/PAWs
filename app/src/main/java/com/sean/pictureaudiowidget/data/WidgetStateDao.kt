package com.sean.pictureaudiowidget.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WidgetStateDao {
    @Query("SELECT * FROM widget_state WHERE widgetId = :widgetId")
    suspend fun get(widgetId: Int): WidgetStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WidgetStateEntity)

    @Query("DELETE FROM widget_state WHERE widgetId = :widgetId")
    suspend fun delete(widgetId: Int)
}
