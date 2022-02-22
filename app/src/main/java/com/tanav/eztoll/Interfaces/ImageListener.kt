package com.tanav.eztoll.Interfaces

import android.graphics.Bitmap
import android.net.Uri


interface ImageListener {

    fun onImageLoaded(error: Boolean, uri: Uri?, bitmap: Bitmap?)
}