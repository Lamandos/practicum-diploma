package ru.practicum.android.diploma.util.debounce

object DebounceFactory {

    // Создает Debounce для поисковых запросов (300ms задержка)
    fun createSearchDebounce(): Debounce {
        return CoroutinesDebounce(DebounceConfig.SEARCH_DELAY)
    }

    // Создает Debounce для защиты от множественных кликов (500ms задержка)
    fun createClickDebounce(): Debounce {
        return CoroutinesDebounce(DebounceConfig.CLICK_DELAY)
    }

    // Создает Debounce для ввода текста (150ms задержка)
    fun createTypingDebounce(): Debounce {
        return CoroutinesDebounce(DebounceConfig.TYPING_DELAY)
    }

    // Создает кастомный Debounce с указанной задержкой
    fun createDebounce(delayMs: Long): Debounce {
        return CoroutinesDebounce(delayMs)
    }
}
