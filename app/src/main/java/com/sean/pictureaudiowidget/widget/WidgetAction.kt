package com.sean.pictureaudiowidget.widget

enum class WidgetAction(val wireValue: String) {
    OPEN_VIEWER("open_viewer"),
    PLAY_CURRENT("play_current"),
    NEXT("next"),
    SORT("sort");

    companion object {
        fun from(value: String?): WidgetAction? = entries.firstOrNull { it.wireValue == value }
    }
}
