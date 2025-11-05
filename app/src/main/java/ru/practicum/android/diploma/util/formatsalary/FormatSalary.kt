package ru.practicum.android.diploma.util.formatsalary

import ru.practicum.android.diploma.domain.models.vacancy.Salary

fun formatSalary(salary: Salary?): String {
    return salary?.let {
        val from = it.from?.let { from -> "от ${formatNumber(from)}" } ?: ""
        val to = it.to?.let { to -> "до ${formatNumber(to)}" } ?: ""
        val currency = getCurrencySymbol(it.currency ?: "")

        listOf(from, to).filter { it.isNotEmpty() }.joinToString(" ") + " $currency"
    } ?: ""
}

private fun formatNumber(number: Int): String {
    return String.format("%,d", number).replace(',', ' ')
}

private fun getCurrencySymbol(currency: String): String {
    return when (currency) {
        "RUR", "RUB" -> "₽"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currency
    }
}
