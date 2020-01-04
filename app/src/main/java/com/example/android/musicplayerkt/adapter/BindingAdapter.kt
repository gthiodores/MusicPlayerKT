package com.example.android.musicplayerkt.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.android.musicplayerkt.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("formatDuration")
fun TextView.formatDuration(duration : Long) {
    val minutes = duration / 60000
    val seconds = (duration / 1000) % 60

    var minuteString = minutes.toString()
    when (minuteString.length) {
        1 -> minuteString = "0$minuteString"
    }

    var secondsString = seconds.toString()
    when (secondsString.length) {
        1 -> secondsString = "0$secondsString"
    }

    this.text = resources.getString(R.string.song_duration, minuteString, secondsString)
}

@BindingAdapter("showView")
fun CardView.showView(show : Boolean) {
    when (show) {
        true -> this.visibility = View.VISIBLE
        else -> this.visibility = View.GONE
    }
}

@BindingAdapter("setPausePlayImage")
fun CircleImageView.setPausePlayImage(isPlaying : Boolean) {
    when (isPlaying) {
        true -> Glide.with(this).load(R.drawable.ic_pause_circle_outline_black_24dp).into(this)
        else -> Glide.with(this).load(R.drawable.ic_play_circle_outline_black_24dp).into(this)
    }
}

@BindingAdapter("showFab")
fun FloatingActionButton.showFab(isPlaying: Boolean) {
    when (isPlaying) {
        true -> this.hide()
        else -> this.show()
    }
}

@BindingAdapter("favouriteMeter")
fun ImageView.favouriteMeter(favourite : Boolean) {
    when (favourite) {
        true -> Glide.with(this).load(R.drawable.ic_favorite_16dp).into(this)
        else ->Glide.with(this).load(R.drawable.ic_favorite_border_16).into(this)
    }
}