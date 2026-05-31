package com.sean.pictureaudiowidget.ui

import com.sean.pictureaudiowidget.media.SourceFolder

data class SourceFolderNode(
    val path: String,
    val name: String,
    val directItemCount: Int,
    val totalItemCount: Int,
    val children: List<SourceFolderNode>,
) {
    val isLeaf: Boolean get() = children.isEmpty()
}

data class VisibleSourceFolderNode(
    val path: String,
    val name: String,
    val depth: Int,
    val totalItemCount: Int,
    val isExpandable: Boolean,
    val isExpanded: Boolean,
    val isChecked: Boolean,
    val selectedDescendantCount: Int,
)

object SourceFolderTreeBuilder {
    fun build(folders: List<SourceFolder>): List<SourceFolderNode> {
        val root = MutableNode(path = "", name = "", directItemCount = 0)
        folders.forEach { folder ->
            val segments = folder.path.trim('/').split('/').filter { it.isNotBlank() }
            if (segments.isEmpty()) return@forEach
            var current = root
            var currentPath = ""
            segments.forEachIndexed { index, segment ->
                currentPath = if (currentPath.isBlank()) segment else "$currentPath/$segment"
                val child = current.children.getOrPut(segment) {
                    MutableNode(path = currentPath, name = segment, directItemCount = 0)
                }
                current = child
                if (index == segments.lastIndex) {
                    child.directItemCount += folder.itemCount
                }
            }
        }
        return root.children.values.map { it.toImmutable() }.sortedBy { it.name.lowercase() }
    }

    fun flatten(
        roots: List<SourceFolderNode>,
        expandedPaths: Set<String>,
        selectedLeafPaths: Set<String>,
    ): List<VisibleSourceFolderNode> {
        return buildList {
            roots.forEach { appendNode(it, 0, expandedPaths, selectedLeafPaths) }
        }
    }

    fun collectLeafPaths(nodes: List<SourceFolderNode>, selectedPaths: Set<String>): Set<String> {
        val byPath = mutableMapOf<String, SourceFolderNode>()
        fun index(node: SourceFolderNode) {
            byPath[node.path] = node
            node.children.forEach(::index)
        }
        nodes.forEach(::index)
        return selectedPaths.flatMapTo(sortedSetOf()) { path ->
            byPath[path]?.leafPaths().orEmpty()
        }
    }

    private fun MutableList<VisibleSourceFolderNode>.appendNode(
        node: SourceFolderNode,
        depth: Int,
        expandedPaths: Set<String>,
        selectedLeafPaths: Set<String>,
    ) {
        val leafPaths = node.leafPaths()
        val selectedCount = leafPaths.count { selectedLeafPaths.contains(it) }
        val isChecked = selectedCount == leafPaths.size && leafPaths.isNotEmpty()
        val expanded = expandedPaths.contains(node.path)
        add(
            VisibleSourceFolderNode(
                path = node.path,
                name = node.name,
                depth = depth,
                totalItemCount = node.totalItemCount,
                isExpandable = node.children.isNotEmpty(),
                isExpanded = expanded,
                isChecked = isChecked,
                selectedDescendantCount = selectedCount,
            )
        )
        if (expanded) {
            node.children.sortedBy { it.name.lowercase() }.forEach { child ->
                appendNode(child, depth + 1, expandedPaths, selectedLeafPaths)
            }
        }
    }

    private fun SourceFolderNode.leafPaths(): Set<String> {
        if (children.isEmpty()) return setOf(path)
        return children.flatMapTo(linkedSetOf()) { it.leafPaths() }
    }

    private class MutableNode(
        val path: String,
        val name: String,
        var directItemCount: Int,
        val children: MutableMap<String, MutableNode> = linkedMapOf(),
    ) {
        fun toImmutable(): SourceFolderNode {
            val immutableChildren = children.values.map { it.toImmutable() }
            return SourceFolderNode(
                path = path,
                name = name,
                directItemCount = directItemCount,
                totalItemCount = directItemCount + immutableChildren.sumOf { it.totalItemCount },
                children = immutableChildren,
            )
        }
    }
}
