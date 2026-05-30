package com.sean.pictureaudiowidget.widget

object WidgetViewStateFactory {
    fun create(snapshot: WidgetSnapshot): WidgetViewState {
        val current = snapshot.currentItem
        return if (current == null) {
            WidgetViewState(
                title = "No media found",
                subtitle = "Grant permissions and refresh from the app.",
                sortLabel = "Sort: ${snapshot.sortMode.name}",
                imageUri = null,
                openImageEnabled = false,
                openAudioEnabled = false,
            )
        } else {
            WidgetViewState(
                title = current.displayTitle,
                subtitle = "Items: ${snapshot.totalItems} • ${current.pairingConfidence.name}",
                sortLabel = "Sort: ${snapshot.sortMode.name}",
                imageUri = current.imageUri,
                openImageEnabled = current.hasImage,
                openAudioEnabled = current.hasAudio,
            )
        }
    }
}
