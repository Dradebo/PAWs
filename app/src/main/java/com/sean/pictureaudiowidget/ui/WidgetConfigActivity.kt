package com.sean.pictureaudiowidget.ui

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sean.pictureaudiowidget.app.appContainer
import com.sean.pictureaudiowidget.widget.PictureAudioWidgetProvider
import kotlinx.coroutines.launch

class WidgetConfigActivity : AppCompatActivity() {
    private lateinit var statusText: TextView
    private lateinit var listView: ListView
    private lateinit var saveButton: Button
    private lateinit var progress: ProgressBar
    private var folders: List<String> = emptyList()
    private var widgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        setResult(RESULT_CANCELED)
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContentView(buildContentView())
        saveButton.setOnClickListener { saveSelection() }
        loadFolders()
    }

    private fun buildContentView(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24.dp(), 24.dp(), 24.dp(), 24.dp())
            setBackgroundColor(0xFF080B12.toInt())
        }

        root.addView(TextView(this).apply {
            text = "Choose widget folders"
            setTextColor(0xFFF8FAFC.toInt())
            textSize = 28f
            gravity = Gravity.START
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })

        root.addView(TextView(this).apply {
            text = "Select the source folder(s) this widget should browse. The widget will ignore other media."
            setTextColor(0xFFCBD5E1.toInt())
            textSize = 15f
            setPadding(0, 8.dp(), 0, 18.dp())
        })

        statusText = TextView(this).apply {
            text = "Loading folders…"
            setTextColor(0xFF94A3B8.toInt())
            textSize = 14f
        }
        root.addView(statusText)

        progress = ProgressBar(this).apply { isIndeterminate = true }
        root.addView(progress, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply { gravity = Gravity.CENTER_HORIZONTAL; topMargin = 12.dp() })

        listView = ListView(this).apply {
            choiceMode = ListView.CHOICE_MODE_MULTIPLE
            visibility = View.GONE
            cacheColorHint = 0x00000000
        }
        root.addView(listView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f,
        ).apply { topMargin = 12.dp() })

        saveButton = Button(this).apply {
            text = "Save widget folders"
            isAllCaps = false
            isEnabled = false
        }
        root.addView(saveButton, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            52.dp(),
        ).apply { topMargin = 14.dp() })

        return root
    }

    private fun loadFolders() {
        lifecycleScope.launch {
            val state = appContainer.widgetStateStore.getOrCreate(widgetId)
            val selected = state.selectedSourceBucketSet()
            folders = appContainer.mediaCatalogRepository.loadCatalog()
                .mapNotNull { it.bucketName?.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
                .sortedWith(compareBy<String> { it.substringAfterLast('/').lowercase() }.thenBy { it.lowercase() })

            progress.visibility = View.GONE
            listView.visibility = View.VISIBLE
            saveButton.isEnabled = folders.isNotEmpty()

            if (folders.isEmpty()) {
                statusText.text = "No media folders found. Grant media access in PAWs, then try again."
                return@launch
            }

            statusText.text = "${folders.size} folders found. Select at least one."
            listView.adapter = ArrayAdapter(this@WidgetConfigActivity, com.sean.pictureaudiowidget.R.layout.folder_choice_item, folders.map { it.labelForDisplay() })
            folders.forEachIndexed { index, bucket ->
                listView.setItemChecked(index, selected.contains(bucket))
            }
        }
    }

    private fun saveSelection() {
        val selected = folders.filterIndexed { index, _ -> listView.isItemChecked(index) }.toSet()
        if (selected.isEmpty()) {
            Toast.makeText(this, "Select at least one folder for this widget.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            appContainer.widgetStateStore.setSelectedSourceBuckets(widgetId, selected)
            PictureAudioWidgetProvider.updateWidget(this@WidgetConfigActivity, widgetId)
            val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            setResult(RESULT_OK, result)
            finish()
        }
    }

    private fun String.labelForDisplay(): String {
        val cleaned = trim('/').ifBlank { this }
        val short = cleaned.substringAfterLast('/').ifBlank { cleaned }
        return "$short\n$cleaned"
    }

    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()
}
