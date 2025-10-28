package ru.practicum.android.diploma.util.debounce

interface Debounce {
    fun submit(task: () -> Unit)
    fun cancel()
}
