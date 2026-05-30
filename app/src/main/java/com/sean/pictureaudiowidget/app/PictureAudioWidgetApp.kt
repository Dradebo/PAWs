package com.sean.pictureaudiowidget.app

import android.app.Application

class PictureAudioWidgetApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
