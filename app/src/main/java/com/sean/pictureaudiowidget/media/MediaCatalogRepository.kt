package com.sean.pictureaudiowidget.media

interface MediaCatalogRepository {
    suspend fun loadCatalog(): List<WidgetMediaItem>

    suspend fun loadCatalog(sourceBuckets: Set<String>): List<WidgetMediaItem> {
        if (sourceBuckets.isEmpty()) return emptyList()
        val normalized = sourceBuckets.map { it.trim().trim('/') }.toSet()
        return loadCatalog().filter { item ->
            item.bucketName?.trim()?.trim('/')?.let { bucket -> normalized.contains(bucket) } == true
        }
    }

    suspend fun listSourceFolders(): List<SourceFolder> {
        return loadCatalog()
            .mapNotNull { item -> item.bucketName?.trim().takeUnless { it.isNullOrEmpty() } }
            .groupingBy { it }
            .eachCount()
            .map { (path, count) -> SourceFolder(path = path, itemCount = count) }
            .sortedBy { it.path.lowercase() }
    }
}
