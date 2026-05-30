package com.sean.pictureaudiowidget.media

interface MediaCatalogRepository {
    suspend fun loadCatalog(): List<WidgetMediaItem>
}
