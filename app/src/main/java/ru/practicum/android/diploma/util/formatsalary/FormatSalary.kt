package ru.practicum.android.diploma.util.formatsalary

import ru.practicum.android.diploma.domain.models.vacancy.Salary
import java.util.Locale

fun formatSalary(salary: Salary?): String {
    return salary?.let {
        val from = it.from?.let { from -> "от ${formatNumber(from)}" } ?: ""
        val to = it.to?.let { to -> "до ${formatNumber(to)}" } ?: ""
        val currency = getCurrencySymbol(it.currency ?: "")

        listOf(from, to).filter { it.isNotEmpty() }.joinToString(" ") + " $currency"
    } ?: ""
}

private fun formatNumber(number: Int): String {
    return String.format(Locale.getDefault(), "%,d", number).replace(',', ' ')
}

private fun getCurrencySymbol(currency: String): String {
    return when (currency.uppercase()) {
        "RUR", "RUB" -> "₽"
        "BYR", "BYN" -> "Br"
        "USD" -> "$"
        "EUR" -> "€"
        "KZT" -> "₸"
        "UAH" -> "₴"
        "AZN" -> "₼"
        "UZS" -> "so'm"
        "GEL" -> "₾"
        "KGS", "KGT" -> "с"
        else -> currency
    }
}
