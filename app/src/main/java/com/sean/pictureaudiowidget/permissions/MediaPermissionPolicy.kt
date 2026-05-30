package com.sean.pictureaudiowidget.permissions

import android.Manifest
import android.os.Build

object MediaPermissionPolicy {
    fun requiredPermissions(sdkInt: Int = Build.VERSION.SDK_INT): Array<String> {
        return if (sdkInt >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}
