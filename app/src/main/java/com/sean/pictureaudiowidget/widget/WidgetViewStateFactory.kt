package com.sean.pictureaudiowidget.widget

object WidgetViewStateFactory {
    fun create(snapshot: WidgetSnapshot): WidgetViewState {
        val current = snapshot.currentItem
        return if (current == null) {
            WidgetViewState(
                title = "Ready to browse",
                subtitle = "Grant media permissions, then refresh.",
                sortLabel = snapshot.sortMode.label(),
                imageUri = null,
                openImageEnabled = false,
                openAudioEnabled = false,
            )
        } else {
            WidgetViewState(
                title = current.displayTitle.cleanMediaTitle(),
                subtitle = "${snapshot.totalItems.compactCount()} items • ${current.pairingConfidence.friendlyLabel()}",
                sortLabel = snapshot.sortMode.label(),
                imageUri = current.imageUri,
                openImageEnabled = current.hasImage,
                openAudioEnabled = current.hasAudio,
            )
        }
    }

    private fun com.sean.pictureaudiowidget.media.SortMode.label(): String {
        return name.lowercase().replaceFirstChar { it.titlecase() }
    }

    private fun com.sean.pictureaudiowidget.media.PairingConfidence.friendlyLabel(): String {
        return when (this) {
            com.sean.pictureaudiowidget.media.PairingConfidence.STRONG -> "image + audio"
            com.sean.pictureaudiowidget.media.PairingConfidence.MEDIUM -> "likely pair"
            com.sean.pictureaudiowidget.media.PairingConfidence.WEAK -> "loose pair"
            com.sean.pictureaudiowidget.media.PairingConfidence.NONE -> "image only"
        }
    }

    private fun String.cleanMediaTitle(): String {
        val withoutExtension = substringBeforeLast('.', this)
            .replace('_', ' ')
            .replace('-', ' ')
            .replace(Regex("""\s+"""), " ")
            .trim()
        val cleaned = withoutExtension.ifBlank { this }
        return if (cleaned.length <= 30) cleaned else cleaned.take(27).trimEnd() + "…"
    }

    private fun Int.compactCount(): String {
        return when {
            this >= 1_000_000 -> "${this / 1_000_000}.${(this % 1_000_000) / 100_000}M"
            this >= 10_000 -> "${this / 1_000}.${(this % 1_000) / 100}k"
            else -> toString()
        }
    }
}
