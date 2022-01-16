package ru.dbuzin.dev.sbertestapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.dbuzin.dev.sbertestapp.data.network.ExchangeRateApi
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    private val timeout = 15L

    @Singleton
    @Provides
    fun provideExchangeRateApi(): ExchangeRateApi = Retrofit.Builder()
        .baseUrl(ExchangeRateApi.BASE_URL)
        .client(
            OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .callTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ExchangeRateApi::class.java)
}