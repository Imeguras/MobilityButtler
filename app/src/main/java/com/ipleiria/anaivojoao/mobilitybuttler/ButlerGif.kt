package com.ipleiria.anaivojoao.mobilitybuttler

import android.widget.ImageView
import com.bumptech.glide.Glide

class ButlerGif(private val activity: MainActivity) {
    private val gifImageView: ImageView = activity.findViewById(R.id.gifImageView)

    fun butlerSpeakGif() {
        activity.runOnUiThread(Runnable { Glide.with(activity).load("file:///android_asset/butler_speaking.gif").into(gifImageView) })
    }

    fun butlerStopSpeakGif() {
        activity.runOnUiThread(Runnable { Glide.with(activity).load("file:///android_asset/butler_image.png").into(gifImageView) })
    }
}