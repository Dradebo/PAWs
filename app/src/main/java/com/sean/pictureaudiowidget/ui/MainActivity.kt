package com.sean.pictureaudiowidget.ui

import android.content.pm.PackageManager
import android.os.Bundle
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
    private lateinit var permissionButton: Button
    private lateinit var refreshButton: Button

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        render()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusView = findViewById(R.id.statusText)
        permissionButton = findViewById(R.id.permissionButton)
        refreshButton = findViewById(R.id.refreshButton)

        permissionButton.setOnClickListener {
            requestPermissions.launch(MediaPermissionPolicy.requiredPermissions())
        }
        refreshButton.setOnClickListener {
            lifecycleScope.launch {
                PictureAudioWidgetProvider.refreshAll(this@MainActivity)
                render()
            }
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
            val state = MainScreenStateFactory.create(hasPermissions, indexedItems)
            statusView.text = state.status
            permissionButton.isEnabled = state.showPermissionButton
            permissionButton.visibility = if (state.showPermissionButton) android.view.View.VISIBLE else android.view.View.GONE
            refreshButton.isEnabled = state.showRefresh
        }
    }
}
