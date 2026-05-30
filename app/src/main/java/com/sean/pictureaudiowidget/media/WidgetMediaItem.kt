package com.sean.pictureaudiowidget.media

data class WidgetMediaItem(
    val id: String,
    val imageUri: String?,
    val audioUri: String?,
    val displayTitle: String,
    val sizeBytes: Long,
    val modifiedAtEpochMillis: Long,
    val bucketName: String?,
    val pairingConfidence: PairingConfidence,
) {
    val hasImage: Boolean get() = !imageUri.isNullOrBlank()
    val hasAudio: Boolean get() = !audioUri.isNullOrBlank()
}
