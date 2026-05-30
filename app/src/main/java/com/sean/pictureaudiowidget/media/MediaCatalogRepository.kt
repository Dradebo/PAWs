package com.sean.pictureaudiowidget.media

interface MediaCatalogRepository {
    suspend fun loadCatalog(): List<WidgetMediaItem>

    suspend fun loadCatalog(sourceBuckets: Set<String>): List<WidgetMediaItem> {
        if (sourceBuckets.isEmpty()) return emptyList()
        return loadCatalog().filter { item ->
            item.bucketName?.let { bucket -> sourceBuckets.contains(bucket) } == true
        }
    }
}
