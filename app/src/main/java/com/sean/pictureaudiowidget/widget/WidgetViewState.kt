package com.sean.pictureaudiowidget.widget

data class WidgetViewState(
    val title: String,
    val subtitle: String,
    val sortLabel: String,
    val imageUri: String?,
    val openImageEnabled: Boolean,
    val openAudioEnabled: Boolean,
)
