package ru.practicum.android.diploma.util.ImageLoader

import android.widget.ImageView

interface ImageLoader {
    fun loadImage(url: String, imageView: ImageView, placeholder: Int)
}
