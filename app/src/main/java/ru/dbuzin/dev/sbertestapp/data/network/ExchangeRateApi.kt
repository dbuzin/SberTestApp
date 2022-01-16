package ru.dbuzin.dev.sbertestapp.data.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.dbuzin.dev.sbertestapp.domain.model.LatestRates

interface ExchangeRateApi {

    @GET("latest")
    suspend fun getLatest(
        @Query("get") rates: String,
        @Query("pairs") pairs: String,
        @Query("key") apiKey: String,
    ): LatestRates?

    companion object {
        const val BASE_URL = "https://currate.ru/api/"
    }
}