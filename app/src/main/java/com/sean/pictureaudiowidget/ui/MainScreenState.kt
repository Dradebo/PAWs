package com.sean.pictureaudiowidget.ui

data class MainScreenState(
    val status: String,
    val librarySummary: String,
    val sourceSummary: String,
    val workflowSummary: String,
    val showRefresh: Boolean,
    val showPermissionButton: Boolean,
    val showFolderBrowserButton: Boolean,
)
