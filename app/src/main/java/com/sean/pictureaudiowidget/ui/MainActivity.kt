package com.sean.pictureaudiowidget.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.sean.pictureaudiowidget.R
import com.sean.pictureaudiowidget.app.appContainer
import com.sean.pictureaudiowidget.permissions.MediaPermissionPolicy
import com.sean.pictureaudiowidget.widget.PictureAudioWidgetProvider
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var statusView: TextView
    private lateinit var librarySummaryView: TextView
    private lateinit var sourceSummaryView: TextView
    private lateinit var workflowSummaryView: TextView
    private lateinit var permissionButton: Button
    private lateinit var refreshButton: Button
    private lateinit var folderBrowserButton: Button

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        render()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusView = findViewById(R.id.statusText)
        librarySummaryView = findViewById(R.id.librarySummaryText)
        sourceSummaryView = findViewById(R.id.sourceSummaryText)
        workflowSummaryView = findViewById(R.id.workflowSummaryText)
        permissionButton = findViewById(R.id.permissionButton)
        refreshButton = findViewById(R.id.refreshButton)
        folderBrowserButton = findViewById(R.id.folderBrowserButton)

        permissionButton.setOnClickListener {
            requestPermissions.launch(MediaPermissionPolicy.requiredPermissions())
        }
        refreshButton.setOnClickListener {
            lifecycleScope.launch {
                PictureAudioWidgetProvider.refreshAll(this@MainActivity)
                render()
            }
        }
        folderBrowserButton.setOnClickListener {
            startActivity(Intent(this, WidgetConfigActivity::class.java).putExtra(WidgetConfigActivity.EXTRA_PREVIEW_ONLY, true))
        }
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun render() {
        val hasPermissions = MediaPermissionPolicy.requiredPermissions().all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }

        lifecycleScope.launch {
            val indexedItems = if (hasPermissions) appContainer.mediaCatalogRepository.loadCatalog().size else 0
            val sourceFolderCount = if (hasPermissions) appContainer.mediaCatalogRepository.listSourceFolders().size else 0
            val state = MainScreenStateFactory.create(hasPermissions, indexedItems, sourceFolderCount)
            statusView.text = state.status
            librarySummaryView.text = state.librarySummary
            sourceSummaryView.text = state.sourceSummary
            workflowSummaryView.text = state.workflowSummary
            permissionButton.isEnabled = state.showPermissionButton
            permissionButton.visibility = if (state.showPermissionButton) View.VISIBLE else View.GONE
            refreshButton.isEnabled = state.showRefresh
            refreshButton.visibility = if (state.showRefresh) View.VISIBLE else View.GONE
            folderBrowserButton.visibility = if (state.showFolderBrowserButton) View.VISIBLE else View.GONE
        }
    }
}
