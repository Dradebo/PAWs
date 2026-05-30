package com.sean.pictureaudiowidget.widget

object WidgetViewStateFactory {
    fun create(snapshot: WidgetSnapshot): WidgetViewState {
        val current = snapshot.currentItem
        return when {
            snapshot.selectedSourceCount == 0 -> WidgetViewState(
                title = "Choose folders",
                subtitle = "Configure this widget source",
                sortLabel = snapshot.sortMode.label(),
                imageUri = null,
                openCurrentEnabled = false,
            )
            current == null -> WidgetViewState(
                title = "No media found",
                subtitle = "${snapshot.selectedSourceCount} source folder(s)",
                sortLabel = snapshot.sortMode.label(),
                imageUri = null,
                openCurrentEnabled = false,
            )
            else -> WidgetViewState(
                title = current.displayTitle.cleanMediaTitle(),
                subtitle = "${snapshot.totalItems.compactCount()} items • ${current.bucketName.orEmpty().folderLabel()}",
                sortLabel = snapshot.sortMode.label(),
                imageUri = current.imageUri,
                openCurrentEnabled = current.hasImage || current.hasAudio,
            )
        }
    }

    private fun com.sean.pictureaudiowidget.media.SortMode.label(): String {
        return name.lowercase().replaceFirstChar { it.titlecase() }
    }

    private fun String.cleanMediaTitle(): String {
        val withoutExtension = substringBeforeLast('.', this)
            .replace('_', ' ')
            .replace('-', ' ')
            .replace(Regex("""\s+"""), " ")
            .trim()
        val cleaned = withoutExtension.ifBlank { this }
        return if (cleaned.length <= 34) cleaned else cleaned.take(31).trimEnd() + "…"
    }

    private fun String.folderLabel(): String {
        val trimmed = trim('/').ifBlank { "selected folder" }
        val last = trimmed.substringAfterLast('/').ifBlank { trimmed }
        return if (last.length <= 22) last else last.take(19).trimEnd() + "…"
    }

    private fun Int.compactCount(): String {
        return when {
            this >= 1_000_000 -> "${this / 1_000_000}.${(this % 1_000_000) / 100_000}M"
            this >= 10_000 -> "${this / 1_000}.${(this % 1_000) / 100}k"
            else -> toString()
        }
    }
}
