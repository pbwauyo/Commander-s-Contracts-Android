package com.example.commanderscontracts

import android.graphics.Bitmap

interface OnSignedCaptureListener {

    fun onSignatureCaptured(bitmap: Bitmap, fileUri: String)
}