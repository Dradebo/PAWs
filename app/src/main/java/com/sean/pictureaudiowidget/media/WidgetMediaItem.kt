package com.sean.pictureaudiowidget.media

data class WidgetMediaItem(
    val id: String,
    val imageUri: String?,
    val audioUri: String?,
    val videoUri: String?,
    val displayTitle: String,
    val sizeBytes: Long,
    val modifiedAtEpochMillis: Long,
    val bucketName: String?,
    val pairingConfidence: PairingConfidence,
) {
    val hasImage: Boolean get() = !imageUri.isNullOrBlank()
    val hasAudio: Boolean get() = !audioUri.isNullOrBlank()
    val hasVideo: Boolean get() = !videoUri.isNullOrBlank()
    val canOpenViewer: Boolean get() = hasImage || hasAudio || hasVideo
    val canPlay: Boolean get() = hasAudio || hasVideo
    val previewUri: String? get() = imageUri
}
