package ru.practicum.android.diploma.util.debounce

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CoroutinesDebounce(
    private val delay: Long,
    private val coroutineContext: CoroutineContext = Dispatchers.Main
) : Debounce {

    private var debounceJob: Job? = null

    override fun submit(task: () -> Unit) {
        debounceJob?.cancel()
        debounceJob = CoroutineScope(coroutineContext).launch {
            delay(delay)
            task()
        }
    }

    override fun cancel() {
        debounceJob?.cancel()
        debounceJob = null
    }
}
