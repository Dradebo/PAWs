package com.sean.pictureaudiowidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.RemoteViews
import com.sean.pictureaudiowidget.R
import com.sean.pictureaudiowidget.app.appContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PictureAudioWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            updateWidget(context, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        CoroutineScope(Dispatchers.IO).launch {
            appWidgetIds.forEach { appWidgetId ->
                context.appContainer.widgetCoordinator.clear(appWidgetId)
            }
        }
    }

    companion object {
        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, PictureAudioWidgetProvider::class.java))
            ids.forEach { updateWidget(context, it) }
        }

        fun updateWidget(context: Context, appWidgetId: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                val snapshot = context.appContainer.widgetCoordinator.snapshot(appWidgetId)
                val viewState = WidgetViewStateFactory.create(snapshot)
                val appWidgetManager = AppWidgetManager.getInstance(context)
                appWidgetManager.updateAppWidget(appWidgetId, buildViews(context, appWidgetId, viewState))
            }
        }

        private fun buildViews(
            context: Context,
            appWidgetId: Int,
            viewState: WidgetViewState,
        ): RemoteViews {
            return RemoteViews(context.packageName, R.layout.widget_picture_audio).apply {
                setTextViewText(R.id.widgetTitle, viewState.title)
                setTextViewText(R.id.widgetSortMode, viewState.sortLabel)
                setTextViewText(R.id.widgetItemTitle, viewState.subtitle)
                setTextViewText(R.id.buttonOpenViewer, viewState.viewerLabel)
                setTextViewText(R.id.buttonPlay, viewState.playLabel)
                loadPreviewBitmap(context, viewState.previewUri)?.let {
                    setImageViewBitmap(R.id.widgetPreview, it)
                } ?: setImageViewResource(R.id.widgetPreview, android.R.drawable.ic_menu_gallery)

                setBoolean(R.id.buttonOpenViewer, "setEnabled", viewState.viewerEnabled)
                setBoolean(R.id.buttonPlay, "setEnabled", viewState.playEnabled)
                setBoolean(R.id.buttonNext, "setEnabled", viewState.nextEnabled)
                setViewVisibility(R.id.buttonPlay, if (viewState.playVisible) android.view.View.VISIBLE else android.view.View.GONE)

                setOnClickPendingIntent(R.id.buttonOpenViewer, actionPendingIntent(context, appWidgetId, WidgetAction.OPEN_VIEWER))
                setOnClickPendingIntent(R.id.buttonPlay, actionPendingIntent(context, appWidgetId, WidgetAction.PLAY_CURRENT))
                setOnClickPendingIntent(R.id.buttonNext, actionPendingIntent(context, appWidgetId, WidgetAction.NEXT))
                setOnClickPendingIntent(R.id.widgetSortMode, actionPendingIntent(context, appWidgetId, WidgetAction.SORT))
                setOnClickPendingIntent(R.id.widgetPreview, actionPendingIntent(context, appWidgetId, WidgetAction.OPEN_VIEWER))
            }
        }

        private fun actionPendingIntent(context: Context, appWidgetId: Int, action: WidgetAction): PendingIntent {
            val intent = Intent(context, WidgetActionReceiver::class.java)
                .putExtra(WidgetActionReceiver.EXTRA_WIDGET_ID, appWidgetId)
                .putExtra(WidgetActionReceiver.EXTRA_ACTION, action.wireValue)
            return PendingIntent.getBroadcast(
                context,
                appWidgetId * 100 + action.ordinal,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        private fun loadPreviewBitmap(context: Context, imageUri: String?): Bitmap? {
            val uri = imageUri?.let(Uri::parse) ?: return null
            return runCatching {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeStream(inputStream, null, bounds)
                    val sampleSize = calculateInSampleSize(bounds, 720, 720)
                    context.contentResolver.openInputStream(uri)?.use { secondStream ->
                        BitmapFactory.decodeStream(secondStream, null, BitmapFactory.Options().apply {
                            inSampleSize = sampleSize
                            inPreferredConfig = Bitmap.Config.RGB_565
                        })
                    }
                }
            }.getOrNull()
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val (height, width) = options.run { outHeight to outWidth }
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                var halfHeight = height / 2
                var halfWidth = width / 2
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize.coerceAtLeast(1)
        }
    }
}
