package ru.dbuzin.dev.sbertestapp.data.repos

import android.content.Context
import javax.inject.Inject

class LastPairRepository @Inject constructor(context: Context) {
    private val preferences = context.getSharedPreferences(LAST_PAIR_PREFS, Context.MODE_PRIVATE)

    fun setPair(currencies: Pair<String, String>) = preferences.edit()
        .putString(VALUE_FROM, currencies.first)
        .putString(VALUE_TO, currencies.second)
        .apply()

    fun getPair(): Pair<String, String> {
        val from = preferences.getString(VALUE_FROM, "USD") ?: "USD"
        val to = preferences.getString(VALUE_TO, "RUB") ?: "RUB"
        return Pair(first = from, second = to)
    }

    companion object {
        const val LAST_PAIR_PREFS = "LAST_PAIR_PREFS"
        private const val VALUE_FROM = "VALUE_FROM"
        private const val VALUE_TO = "VALUE_TO"
    }
}