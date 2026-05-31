package com.sean.pictureaudiowidget.ui

object MainScreenStateFactory {
    fun create(hasPermissions: Boolean, indexedItems: Int, sourceFolderCount: Int): MainScreenState {
        return if (!hasPermissions) {
            MainScreenState(
                status = "Grant media permissions to start indexing local pictures and audio.",
                librarySummary = "Library not available yet.",
                sourceSummary = "Folder browser unlocks after permissions are granted.",
                workflowSummary = "Grant access → browse folders → add widget → choose source folders.",
                showRefresh = false,
                showPermissionButton = true,
                showFolderBrowserButton = false,
            )
        } else {
            MainScreenState(
                status = "Library ready",
                librarySummary = "$indexedItems paired media items across $sourceFolderCount folders.",
                sourceSummary = "Widgets can be scoped to selected source folders instead of all device media.",
                workflowSummary = "Add a widget, choose a parent folder, then use the overlay controls on the home screen.",
                showRefresh = true,
                showPermissionButton = false,
                showFolderBrowserButton = true,
            )
        }
    }
}
