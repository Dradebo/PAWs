package com.sean.pictureaudiowidget.media

enum class SortMode {
    RANDOM,
    SIZE,
    RECENT;

    fun next(): SortMode = when (this) {
        RANDOM -> SIZE
        SIZE -> RECENT
        RECENT -> RANDOM
    }
}
