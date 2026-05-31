package com.sean.pictureaudiowidget.widget

object WidgetViewStateFactory {
    fun create(snapshot: WidgetSnapshot): WidgetViewState {
        val current = snapshot.currentItem
        return when {
            snapshot.selectedSourceCount == 0 -> WidgetViewState(
                title = "Choose folders",
                subtitle = "Configure this widget source",
                sortLabel = snapshot.sortMode.label(),
                previewUri = null,
                viewerEnabled = false,
                viewerLabel = "◱",
                playEnabled = false,
                playVisible = false,
                playLabel = "▶",
                nextEnabled = false,
            )
            current == null -> WidgetViewState(
                title = "No media found",
                subtitle = "${snapshot.selectedSourceCount} source folder(s)",
                sortLabel = snapshot.sortMode.label(),
                previewUri = null,
                viewerEnabled = false,
                viewerLabel = "◱",
                playEnabled = false,
                playVisible = false,
                playLabel = "▶",
                nextEnabled = false,
            )
            else -> WidgetViewState(
                title = current.displayTitle.cleanMediaTitle(),
                subtitle = subtitleFor(current, snapshot.totalItems),
                sortLabel = snapshot.sortMode.label(),
                previewUri = current.previewUri,
                viewerEnabled = current.canOpenViewer,
                viewerLabel = "◱",
                playEnabled = current.canPlay,
                playVisible = current.canPlay,
                playLabel = if (current.hasVideo) "▶" else "▶",
                nextEnabled = snapshot.totalItems > 1,
            )
        }
    }

    private fun subtitleFor(current: com.sean.pictureaudiowidget.media.WidgetMediaItem, totalItems: Int): String {
        val kindLabel = when {
            current.hasVideo -> "video"
            current.hasAudio && current.hasImage -> "photo + sound"
            current.hasAudio -> "sound"
            else -> current.bucketName.orEmpty().folderLabel()
        }
        return "${totalItems.compactCount()} items • $kindLabel"
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
