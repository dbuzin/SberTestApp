package ru.dbuzin.dev.sbertestapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.dbuzin.dev.sbertestapp.data.network.ExchangeRateApi
import ru.dbuzin.dev.sbertestapp.data.repos.ApiKeyRepository
import ru.dbuzin.dev.sbertestapp.data.repos.LastPairRepository
import ru.dbuzin.dev.sbertestapp.data.repos.RatesRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Provides
    @Singleton
    fun provideApiKeyRepository(@ApplicationContext context: Context) = ApiKeyRepository(context)

    @Provides
    @Singleton
    fun provideLastPairRepository(@ApplicationContext context: Context) =
        LastPairRepository(context)

    @Provides
    @Singleton
    fun provideRatesRepository(@ApplicationContext context: Context, api: ExchangeRateApi) =
        RatesRepository(context, api)
}