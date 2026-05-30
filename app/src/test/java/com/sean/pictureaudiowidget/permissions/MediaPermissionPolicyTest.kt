package com.sean.pictureaudiowidget.permissions

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MediaPermissionPolicyTest {
    @Test
    fun `android 33 plus requires split image and audio permissions`() {
        val result = MediaPermissionPolicy.requiredPermissions(34)

        assertThat(result.toList()).containsExactly(
            "android.permission.READ_MEDIA_IMAGES",
            "android.permission.READ_MEDIA_AUDIO",
        ).inOrder()
    }

    @Test
    fun `pre android 33 requires external storage permission`() {
        val result = MediaPermissionPolicy.requiredPermissions(32)

        assertThat(result.toList()).containsExactly("android.permission.READ_EXTERNAL_STORAGE")
    }
}
