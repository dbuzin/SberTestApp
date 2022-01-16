package ru.dbuzin.dev.sbertestapp.data.repos

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import ru.dbuzin.dev.sbertestapp.data.network.ExchangeRateApi
import ru.dbuzin.dev.sbertestapp.domain.model.LatestRates
import javax.inject.Inject

class RatesRepository @Inject constructor(
    context: Context,
    private val ratesApi: ExchangeRateApi
) {
    private val preferences = context.getSharedPreferences(LATEST_RATES_PREFS, Context.MODE_PRIVATE)
    private val gson = Gson()

    suspend fun loadLatestRates(rates: String, pairs: String, key: String): LatestRates? {
        return try {
            val latestRates = ratesApi.getLatest(rates, pairs, key)
            if (latestRates?.data?.isNullOrEmpty() == false)
                latestRates
            else
                null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun cacheLatestRates(latestRates: LatestRates) {
        val json = gson.toJson(latestRates)
        preferences.edit().putString(LATEST_RATES, json).apply()
    }

    fun getCachedRates(): LatestRates? {
        val stringRates = preferences.getString(LATEST_RATES, "")
        return try {
            gson.fromJson(stringRates, LatestRates::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val LATEST_RATES_PREFS = "LATEST_RATES_PREFS"
        const val LATEST_RATES = "LATEST_RATES"
    }
}