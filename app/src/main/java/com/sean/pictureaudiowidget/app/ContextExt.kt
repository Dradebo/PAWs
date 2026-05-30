package com.sean.pictureaudiowidget.app

import android.content.Context

val Context.appContainer: AppContainer
    get() = (applicationContext as PictureAudioWidgetApp).container
