package com.sean.pictureaudiowidget.media

import android.content.ContentResolver
import android.os.Build
import android.provider.MediaStore

class MediaStoreCatalogRepository(
    private val contentResolver: ContentResolver,
) : MediaCatalogRepository {
    @Volatile
    private var cachedCatalog: List<WidgetMediaItem>? = null

    @Volatile
    private var cachedAtMillis: Long = 0L

    override suspend fun loadCatalog(): List<WidgetMediaItem> {
        val now = System.currentTimeMillis()
        val cached = cachedCatalog
        if (cached != null && now - cachedAtMillis < 15_000) return cached

        val images = queryImages()
        val audios = queryAudios()
        val videos = queryVideos()
        return MediaPairer.pair(images, audios, videos)
            .sortedByDescending { it.modifiedAtEpochMillis }
            .also {
                cachedCatalog = it
                cachedAtMillis = now
            }
    }

    private fun queryImages(): List<MediaEntry> {
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val includeRelativePath = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        val projection = buildList {
            add(MediaStore.Images.Media._ID)
            add(MediaStore.Images.Media.DISPLAY_NAME)
            add(MediaStore.Images.Media.SIZE)
            add(MediaStore.Images.Media.DATE_MODIFIED)
            if (includeRelativePath) add(MediaStore.MediaColumns.RELATIVE_PATH)
        }.toTypedArray()

        return contentResolver.query(collection, projection, null, null, null)?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val modifiedIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val bucketIndex = if (includeRelativePath) cursor.getColumnIndex(MediaStore.MediaColumns.RELATIVE_PATH) else -1
            buildList {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    add(
                        MediaEntry.create(
                            id = "image:$id",
                            uri = android.net.Uri.withAppendedPath(collection, id.toString()).toString(),
                            title = cursor.getString(nameIndex).orEmpty(),
                            sizeBytes = cursor.getLong(sizeIndex),
                            modifiedAtEpochMillis = cursor.getLong(modifiedIndex) * 1000,
                            bucketName = cursor.getOptionalString(bucketIndex),
                            mediaKind = MediaKind.IMAGE,
                        )
                    )
                }
            }
        }.orEmpty()
    }

    private fun queryAudios(): List<MediaEntry> {
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val includeRelativePath = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        val projection = buildList {
            add(MediaStore.Audio.Media._ID)
            add(MediaStore.Audio.Media.DISPLAY_NAME)
            add(MediaStore.Audio.Media.SIZE)
            add(MediaStore.Audio.Media.DATE_MODIFIED)
            if (includeRelativePath) add(MediaStore.MediaColumns.RELATIVE_PATH)
        }.toTypedArray()

        return contentResolver.query(collection, projection, null, null, null)?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val modifiedIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val bucketIndex = if (includeRelativePath) cursor.getColumnIndex(MediaStore.MediaColumns.RELATIVE_PATH) else -1
            buildList {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    add(
                        MediaEntry.create(
                            id = "audio:$id",
                            uri = android.net.Uri.withAppendedPath(collection, id.toString()).toString(),
                            title = cursor.getString(nameIndex).orEmpty(),
                            sizeBytes = cursor.getLong(sizeIndex),
                            modifiedAtEpochMillis = cursor.getLong(modifiedIndex) * 1000,
                            bucketName = cursor.getOptionalString(bucketIndex),
                            mediaKind = MediaKind.AUDIO,
                        )
                    )
                }
            }
        }.orEmpty()
    }

    private fun queryVideos(): List<MediaEntry> {
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val includeRelativePath = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        val projection = buildList {
            add(MediaStore.Video.Media._ID)
            add(MediaStore.Video.Media.DISPLAY_NAME)
            add(MediaStore.Video.Media.SIZE)
            add(MediaStore.Video.Media.DATE_MODIFIED)
            if (includeRelativePath) add(MediaStore.MediaColumns.RELATIVE_PATH)
        }.toTypedArray()

        return contentResolver.query(collection, projection, null, null, null)?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val modifiedIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
            val bucketIndex = if (includeRelativePath) cursor.getColumnIndex(MediaStore.MediaColumns.RELATIVE_PATH) else -1
            buildList {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    add(
                        MediaEntry.create(
                            id = "video:$id",
                            uri = android.net.Uri.withAppendedPath(collection, id.toString()).toString(),
                            title = cursor.getString(nameIndex).orEmpty(),
                            sizeBytes = cursor.getLong(sizeIndex),
                            modifiedAtEpochMillis = cursor.getLong(modifiedIndex) * 1000,
                            bucketName = cursor.getOptionalString(bucketIndex),
                            mediaKind = MediaKind.VIDEO,
                        )
                    )
                }
            }
        }.orEmpty()
    }

    private fun android.database.Cursor.getOptionalString(index: Int): String? {
        return if (index >= 0 && !isNull(index)) getString(index) else null
    }
}
