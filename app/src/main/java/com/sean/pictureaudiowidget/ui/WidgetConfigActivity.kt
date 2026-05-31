package com.sean.pictureaudiowidget.ui

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sean.pictureaudiowidget.R
import com.sean.pictureaudiowidget.app.appContainer
import com.sean.pictureaudiowidget.media.SourceFolder
import com.sean.pictureaudiowidget.widget.PictureAudioWidgetProvider
import kotlinx.coroutines.launch

class WidgetConfigActivity : AppCompatActivity() {
    private lateinit var titleView: TextView
    private lateinit var subtitleView: TextView
    private lateinit var statusText: TextView
    private lateinit var searchInput: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var saveButton: Button
    private lateinit var progress: ProgressBar
    private lateinit var selectCameraButton: Button
    private lateinit var selectDownloadsButton: Button
    private lateinit var clearAllButton: Button

    private lateinit var adapter: FolderTreeAdapter
    private var rootNodes: List<SourceFolderNode> = emptyList()
    private val expandedPaths = mutableSetOf<String>()
    private val selectedLeafPaths = mutableSetOf<String>()
    private var widgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID
    private var previewOnly: Boolean = false
    private var sourceFolderCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config)

        widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        previewOnly = intent?.getBooleanExtra(EXTRA_PREVIEW_ONLY, false) == true

        if (!previewOnly) {
            setResult(RESULT_CANCELED)
            if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish()
                return
            }
        }

        bindViews()
        configureHeader()
        configureRecycler()
        configureActions()
        loadFolders()
    }

    private fun bindViews() {
        titleView = findViewById(R.id.configTitle)
        subtitleView = findViewById(R.id.configSubtitle)
        statusText = findViewById(R.id.statusText)
        searchInput = findViewById(R.id.searchInput)
        recyclerView = findViewById(R.id.folderRecyclerView)
        saveButton = findViewById(R.id.saveButton)
        progress = findViewById(R.id.progressBar)
        selectCameraButton = findViewById(R.id.selectCameraButton)
        selectDownloadsButton = findViewById(R.id.selectDownloadsButton)
        clearAllButton = findViewById(R.id.clearAllButton)
    }

    private fun configureHeader() {
        if (previewOnly) {
            titleView.text = "Browse source folders"
            subtitleView.text = "Preview the folder tree used by widgets. Add a widget to save a scoped source selection."
            saveButton.text = "Done"
        }
    }

    private fun configureRecycler() {
        adapter = FolderTreeAdapter(
            onToggleExpanded = ::toggleExpanded,
            onToggleSelected = ::toggleSelected,
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun configureActions() {
        saveButton.setOnClickListener {
            if (previewOnly) {
                finish()
            } else {
                saveSelection()
            }
        }
        selectCameraButton.setOnClickListener { selectPreset(setOf("Camera", "DCIM", "Pictures", "WhatsApp")) }
        selectDownloadsButton.setOnClickListener { selectPreset(setOf("Download", "Downloads", "Music")) }
        clearAllButton.setOnClickListener {
            selectedLeafPaths.clear()
            renderTree()
        }
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                renderTree()
            }
        })
    }

    private fun loadFolders() {
        lifecycleScope.launch {
            val existingSelected = if (previewOnly) emptySet() else appContainer.widgetStateStore.getOrCreate(widgetId).selectedSourceBucketSet()
            val folders = appContainer.mediaCatalogRepository.listSourceFolders()
            rootNodes = SourceFolderTreeBuilder.build(folders)
            sourceFolderCount = folders.size
            selectedLeafPaths.clear()
            selectedLeafPaths.addAll(existingSelected)
            expandedPaths.clear()
            expandedPaths.addAll(rootNodes.map { it.path })

            progress.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            saveButton.isEnabled = previewOnly || selectedLeafPaths.isNotEmpty()

            if (folders.isEmpty()) {
                statusText.text = "No media folders found. Grant media access in PAWs, then refresh."
                return@launch
            }

            renderTree(totalFolders = folders)
        }
    }

    private fun renderTree(totalFolders: List<SourceFolder>? = null) {
        val query = searchInput.text?.toString()?.trim().orEmpty()
        val visibleRoots = if (query.isBlank()) rootNodes else filterNodes(rootNodes, query)
        val effectiveExpanded = if (query.isBlank()) expandedPaths else indexNodes(visibleRoots).keys
        val visibleItems = SourceFolderTreeBuilder.flatten(visibleRoots, effectiveExpanded, selectedLeafPaths)
        adapter.submitList(visibleItems)
        val folderCount = totalFolders?.size ?: sourceFolderCount
        val selectedCount = selectedLeafPaths.size
        statusText.text = when {
            rootNodes.isEmpty() -> "No media folders found."
            query.isNotBlank() && visibleItems.isEmpty() -> "No folders match \"$query\"."
            else -> "$folderCount folders indexed • $selectedCount selected"
        }
        saveButton.isEnabled = previewOnly || selectedLeafPaths.isNotEmpty()
    }

    private fun toggleExpanded(path: String) {
        if (expandedPaths.contains(path)) expandedPaths.remove(path) else expandedPaths.add(path)
        renderTree()
    }

    private fun toggleSelected(path: String) {
        val leafPaths = SourceFolderTreeBuilder.collectLeafPaths(rootNodes, setOf(path))
        if (leafPaths.isEmpty()) return
        val allSelected = leafPaths.all { selectedLeafPaths.contains(it) }
        if (allSelected) selectedLeafPaths.removeAll(leafPaths) else selectedLeafPaths.addAll(leafPaths)
        renderTree()
    }

    private fun selectPreset(names: Set<String>) {
        val matchingPaths = rootNodes
            .filter { node -> names.any { preset -> node.name.contains(preset, ignoreCase = true) } }
            .map { it.path }
            .toSet()
        selectedLeafPaths.addAll(SourceFolderTreeBuilder.collectLeafPaths(rootNodes, matchingPaths))
        renderTree()
    }

    private fun saveSelection() {
        if (selectedLeafPaths.isEmpty()) {
            Toast.makeText(this, "Select at least one folder for this widget.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            appContainer.widgetStateStore.setSelectedSourceBuckets(widgetId, selectedLeafPaths)
            PictureAudioWidgetProvider.updateWidget(this@WidgetConfigActivity, widgetId)
            val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            setResult(RESULT_OK, result)
            finish()
        }
    }

    private fun filterNodes(nodes: List<SourceFolderNode>, query: String): List<SourceFolderNode> {
        val lowered = query.lowercase()
        return nodes.mapNotNull { node -> filterNode(node, lowered) }
    }

    private fun filterNode(node: SourceFolderNode, query: String): SourceFolderNode? {
        val filteredChildren = node.children.mapNotNull { filterNode(it, query) }
        val matchesSelf = node.name.lowercase().contains(query) || node.path.lowercase().contains(query)
        if (!matchesSelf && filteredChildren.isEmpty()) return null
        return node.copy(children = filteredChildren)
    }

    private fun indexNodes(nodes: List<SourceFolderNode>): Map<String, SourceFolderNode> {
        val map = linkedMapOf<String, SourceFolderNode>()
        fun visit(node: SourceFolderNode) {
            map[node.path] = node
            node.children.forEach(::visit)
        }
        nodes.forEach(::visit)
        return map
    }

    companion object {
        const val EXTRA_PREVIEW_ONLY = "preview_only"
    }
}
