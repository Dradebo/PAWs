package com.sean.pictureaudiowidget.widget

data class WidgetViewState(
    val title: String,
    val subtitle: String,
    val sortLabel: String,
    val previewUri: String?,
    val viewerEnabled: Boolean,
    val viewerLabel: String,
    val playEnabled: Boolean,
    val playVisible: Boolean,
    val playLabel: String,
    val nextEnabled: Boolean,
)
