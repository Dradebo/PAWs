package com.sean.pictureaudiowidget.media

object MediaNameNormalizer {
    fun normalize(rawName: String?): String {
        return rawName
            ?.substringBeforeLast('.', rawName)
            ?.trim()
            ?.lowercase()
            ?.replace(Regex("[^a-z0-9]+"), "")
            .orEmpty()
    }
}
