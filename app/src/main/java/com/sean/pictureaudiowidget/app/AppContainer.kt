package com.sean.pictureaudiowidget.app

import android.content.Context
import androidx.room.Room
import com.sean.pictureaudiowidget.data.AppDatabase
import com.sean.pictureaudiowidget.data.WidgetStateRepository
import com.sean.pictureaudiowidget.data.WidgetStateStore
import com.sean.pictureaudiowidget.media.MediaCatalogRepository
import com.sean.pictureaudiowidget.media.MediaStoreCatalogRepository
import com.sean.pictureaudiowidget.widget.WidgetCoordinator

class AppContainer(context: Context) {
    private val applicationContext = context.applicationContext

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "picture-audio-widget.db").build()
    }

    val mediaCatalogRepository: MediaCatalogRepository by lazy {
        MediaStoreCatalogRepository(applicationContext.contentResolver)
    }

    val widgetStateStore: WidgetStateStore by lazy {
        WidgetStateRepository(database.widgetStateDao())
    }

    val widgetCoordinator: WidgetCoordinator by lazy {
        WidgetCoordinator(mediaCatalogRepository, widgetStateStore)
    }
}
