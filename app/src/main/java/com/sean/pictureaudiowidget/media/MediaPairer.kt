package com.sean.pictureaudiowidget.media

object MediaPairer {
    fun pair(images: List<MediaEntry>, audios: List<MediaEntry>, videos: List<MediaEntry>): List<WidgetMediaItem> {
        val audioBuckets = audios.groupBy { it.bucketName to it.normalizedBaseName }
        val usedAudioIds = linkedSetOf<String>()
        val paired = mutableListOf<WidgetMediaItem>()

        images.forEach { image ->
            val exact = audioBuckets[image.bucketName to image.normalizedBaseName]
                ?.firstOrNull { it.id !in usedAudioIds }

            if (exact != null) {
                usedAudioIds += exact.id
                paired += WidgetMediaItem(
                    id = image.id,
                    imageUri = image.uri,
                    audioUri = exact.uri,
                    videoUri = null,
                    displayTitle = image.title,
                    sizeBytes = image.sizeBytes + exact.sizeBytes,
                    modifiedAtEpochMillis = maxOf(image.modifiedAtEpochMillis, exact.modifiedAtEpochMillis),
                    bucketName = image.bucketName,
                    pairingConfidence = PairingConfidence.STRONG,
                )
            } else {
                paired += WidgetMediaItem(
                    id = image.id,
                    imageUri = image.uri,
                    audioUri = null,
                    videoUri = null,
                    displayTitle = image.title,
                    sizeBytes = image.sizeBytes,
                    modifiedAtEpochMillis = image.modifiedAtEpochMillis,
                    bucketName = image.bucketName,
                    pairingConfidence = PairingConfidence.NONE,
                )
            }
        }

        audios.filterNot { it.id in usedAudioIds }
            .forEach { audio ->
                paired += WidgetMediaItem(
                    id = audio.id,
                    imageUri = null,
                    audioUri = audio.uri,
                    videoUri = null,
                    displayTitle = audio.title,
                    sizeBytes = audio.sizeBytes,
                    modifiedAtEpochMillis = audio.modifiedAtEpochMillis,
                    bucketName = audio.bucketName,
                    pairingConfidence = PairingConfidence.NONE,
                )
            }

        videos.forEach { video ->
            paired += WidgetMediaItem(
                id = video.id,
                imageUri = null,
                audioUri = null,
                videoUri = video.uri,
                displayTitle = video.title,
                sizeBytes = video.sizeBytes,
                modifiedAtEpochMillis = video.modifiedAtEpochMillis,
                bucketName = video.bucketName,
                pairingConfidence = PairingConfidence.NONE,
            )
        }

        return paired
    }
}
