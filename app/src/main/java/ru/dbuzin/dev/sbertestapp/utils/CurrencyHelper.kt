package ru.dbuzin.dev.sbertestapp.utils

object CurrencyHelper {

    fun getAllPairs(): String {
        val builder = StringBuilder()
        val currency = listOf("RUB", "USD", "EUR", "GBP", "CHF", "CNY")
        currency.forEach { first ->
            currency.forEach { second ->
                if (first != second)
                    builder.append(first.plus(second).plus(','))
            }
        }
        return builder.toString()
    }
}