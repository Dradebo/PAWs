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
                WidgetAction.OPEN_CURRENT -> openCurrent(context, appWidgetId)
                WidgetAction.OPEN_IMAGE -> openCurrentImage(context, appWidgetId)
                WidgetAction.OPEN_AUDIO -> openCurrentAudio(context, appWidgetId)
                WidgetAction.NEXT -> {
                    context.appContainer.widgetCoordinator.next(appWidgetId)
                    PictureAudioWidgetProvider.updateWidget(context, appWidgetId)
                }
                WidgetAction.SHUFFLE -> {
                    context.appContainer.widgetCoordinator.shuffle(appWidgetId)
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

    private suspend fun openCurrent(context: Context, appWidgetId: Int) {
        val snapshot = context.appContainer.widgetCoordinator.snapshot(appWidgetId)
        val current = snapshot.currentItem ?: return
        if (current.hasAudio) {
            openCurrentAudio(context, appWidgetId)
        } else {
            openCurrentImage(context, appWidgetId)
        }
    }

    private suspend fun openCurrentImage(context: Context, appWidgetId: Int) {
        val snapshot = context.appContainer.widgetCoordinator.snapshot(appWidgetId)
        val imageUri = snapshot.currentItem?.imageUri ?: return
        val openIntent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(Uri.parse(imageUri), "image/*")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (openIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(openIntent)
        }
    }

    private suspend fun openCurrentAudio(context: Context, appWidgetId: Int) {
        val snapshot = context.appContainer.widgetCoordinator.snapshot(appWidgetId)
        val current = snapshot.currentItem ?: return
        val audioUri = current.audioUri ?: return
        context.startActivity(AudioPlayerContract.createIntent(context, audioUri, current.displayTitle))
    }

    companion object {
        const val EXTRA_ACTION = "extra_action"
        const val EXTRA_WIDGET_ID = "extra_widget_id"
    }
}
