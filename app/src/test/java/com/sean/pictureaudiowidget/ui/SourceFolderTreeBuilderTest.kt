package com.sean.pictureaudiowidget.ui

import com.google.common.truth.Truth.assertThat
import com.sean.pictureaudiowidget.media.SourceFolder
import org.junit.Test

class SourceFolderTreeBuilderTest {
    @Test
    fun `build creates nested roots with aggregated counts`() {
        val tree = SourceFolderTreeBuilder.build(
            listOf(
                SourceFolder("Download/Samples/Bass", 4),
                SourceFolder("Download/Samples/Drums", 2),
                SourceFolder("DCIM/Camera", 3),
            )
        )

        assertThat(tree.map { it.name }).containsExactly("DCIM", "Download")
        val download = tree.first { it.name == "Download" }
        assertThat(download.totalItemCount).isEqualTo(6)
        assertThat(download.children.single().name).isEqualTo("Samples")
    }

    @Test
    fun `collect leaf paths expands parent selection to descendants`() {
        val tree = SourceFolderTreeBuilder.build(
            listOf(
                SourceFolder("Download/Samples/Bass", 4),
                SourceFolder("Download/Samples/Drums", 2),
                SourceFolder("DCIM/Camera", 3),
            )
        )

        val selected = SourceFolderTreeBuilder.collectLeafPaths(tree, setOf("Download"))

        assertThat(selected).containsExactly("Download/Samples/Bass", "Download/Samples/Drums")
    }

    @Test
    fun `flatten marks partially selected ancestors`() {
        val tree = SourceFolderTreeBuilder.build(
            listOf(
                SourceFolder("Download/Samples/Bass", 4),
                SourceFolder("Download/Samples/Drums", 2),
            )
        )

        val visible = SourceFolderTreeBuilder.flatten(
            roots = tree,
            expandedPaths = setOf("Download", "Download/Samples"),
            selectedLeafPaths = setOf("Download/Samples/Bass"),
        )

        val root = visible.first { it.path == "Download" }
        assertThat(root.isChecked).isFalse()
        assertThat(root.selectedDescendantCount).isEqualTo(1)
    }
}
