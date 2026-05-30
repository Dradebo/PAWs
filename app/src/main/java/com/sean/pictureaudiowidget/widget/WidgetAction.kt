package com.sean.pictureaudiowidget.widget

enum class WidgetAction(val wireValue: String) {
    OPEN_IMAGE("open_image"),
    OPEN_AUDIO("open_audio"),
    NEXT("next"),
    SORT("sort");

    companion object {
        fun from(value: String?): WidgetAction? = entries.firstOrNull { it.wireValue == value }
    }
}
