package ru.practicum.android.diploma.util.imageLoader

import android.widget.ImageView

interface ImageLoader {
    fun loadImage(url: String, imageView: ImageView, placeholder: Int)
}
