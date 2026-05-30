package com.sean.pictureaudiowidget.media

data class MediaEntry(
    val id: String,
    val uri: String,
    val title: String,
    val normalizedBaseName: String,
    val sizeBytes: Long,
    val modifiedAtEpochMillis: Long,
    val bucketName: String?,
) {
    companion object {
        fun create(
            id: String,
            uri: String,
            title: String,
            sizeBytes: Long,
            modifiedAtEpochMillis: Long,
            bucketName: String?,
        ): MediaEntry = MediaEntry(
            id = id,
            uri = uri,
            title = title,
            normalizedBaseName = MediaNameNormalizer.normalize(title),
            sizeBytes = sizeBytes,
            modifiedAtEpochMillis = modifiedAtEpochMillis,
            bucketName = bucketName,
        )
    }
}
