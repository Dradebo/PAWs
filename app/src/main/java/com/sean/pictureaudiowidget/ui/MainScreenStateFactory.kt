package com.sean.pictureaudiowidget.ui

object MainScreenStateFactory {
    fun create(hasPermissions: Boolean, indexedItems: Int): MainScreenState {
        return if (!hasPermissions) {
            MainScreenState(
                status = "Grant media permissions to start indexing local pictures and audio.",
                showRefresh = false,
                showPermissionButton = true,
            )
        } else {
            MainScreenState(
                status = "Indexed items available: $indexedItems",
                showRefresh = true,
                showPermissionButton = false,
            )
        }
    }
}
