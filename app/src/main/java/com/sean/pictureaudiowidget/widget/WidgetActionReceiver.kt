package com.sean.pictureaudiowidget.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sean.pictureaudiowidget.app.appContainer
import com.sean.pictureaudiowidget.player.AudioPlayerContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetId = intent.getIntExtra(EXTRA_WIDGET_ID, -1)
        if (appWidgetId == -1) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            when (WidgetAction.from(intent.getStringExtra(EXTRA_ACTION))) {
                WidgetAction.OPEN_VIEWER -> openViewer(context, appWidgetId)
                WidgetAction.PLAY_CURRENT -> playCurrent(context, appWidgetId)
                WidgetAction.NEXT -> {
                    context.appContainer.widgetCoordinator.next(appWidgetId)
                    PictureAudioWidgetProvider.updateWidget(context, appWidgetId)
                }
                WidgetAction.SORT -> {
                    context.appContainer.widgetCoordinator.cycleSort(appWidgetId)
                    PictureAudioWidgetProvider.updateWidget(context, appWidgetId)
                }
                null -> Unit
            }
            pendingResult.finish()
        }
    }

    private suspend fun openViewer(context: Context, appWidgetId: Int) {
        val current = context.appContainer.widgetCoordinator.snapshot(appWidgetId).currentItem ?: return
        when {
            current.hasImage -> openExternal(context, current.imageUri, "image/*")
            current.hasVideo -> openExternal(context, current.videoUri, "video/*")
            current.hasAudio -> openExternal(context, current.audioUri, "audio/*")
        }
    }

    private suspend fun playCurrent(context: Context, appWidgetId: Int) {
        val current = context.appContainer.widgetCoordinator.snapshot(appWidgetId).currentItem ?: return
        when {
            current.hasAudio -> context.startActivity(AudioPlayerContract.createIntent(context, current.audioUri.orEmpty(), current.displayTitle))
            current.hasVideo -> openExternal(context, current.videoUri, "video/*")
        }
    }

    private fun openExternal(context: Context, uriString: String?, mimeType: String) {
        val uri = uriString?.let(Uri::parse) ?: return
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri, mimeType)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_ACTION = "extra_action"
        const val EXTRA_WIDGET_ID = "extra_widget_id"
    }
}
