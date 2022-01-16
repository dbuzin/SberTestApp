package ru.dbuzin.dev.sbertestapp.ui.feature.converter

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.dbuzin.dev.sbertestapp.data.repos.ApiKeyRepository
import ru.dbuzin.dev.sbertestapp.data.repos.LastPairRepository
import ru.dbuzin.dev.sbertestapp.data.repos.RatesRepository
import ru.dbuzin.dev.sbertestapp.ui.base.BaseViewModel
import ru.dbuzin.dev.sbertestapp.utils.CurrencyHelper
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
    keyRepository: ApiKeyRepository,
    private val lastPairRepository: LastPairRepository,
    private val ratesRepository: RatesRepository
): BaseViewModel<Converter.State, Converter.Event, Converter.Effect>(Converter.State()) {
    private val key = keyRepository.getApiKey()
    private val ratesParam = "rates"

    init {
        viewModelScope.launch {
            val lastPair = lastPairRepository.getPair()
            setState { copy(from = lastPair.first, to = lastPair.second) }
            val rates = ratesRepository.loadLatestRates(rates = ratesParam, CurrencyHelper.getAllPairs(), key = key)
            if (rates != null) {
                ratesRepository.cacheLatestRates(rates)
                setState { copy(currentRates = rates) }
            } else {
                val cached = ratesRepository.getCachedRates()
                if (cached != null) {
                    setState { copy(currentRates = cached) }
                    setEffect { Converter.Effect.Offline }
                } else
                    setEffect { Converter.Effect.Error }
            }
        }
    }

    override fun handleEvents(event: Converter.Event) {
        when (event) {
            is Converter.Event.ValueFromChanged -> viewModelScope.launch {
                val newPair = lastPairRepository.getPair().copy(first = event.from)
                if (event.from != state.value.to)
                    lastPairRepository.setPair(newPair)
                val rates = ratesRepository.loadLatestRates(rates = ratesParam, CurrencyHelper.getAllPairs(), key = key)
                if (rates != null) {
                    ratesRepository.cacheLatestRates(rates)
                    setState { copy(from = event.from, currentRates = rates) }
                    setEffect { Converter.Effect.ConnectionRestored }
                } else {
                    setState { copy(from = event.from) }
                    setEffect { Converter.Effect.Offline }
                }
            }
            is Converter.Event.ValueToChanged -> viewModelScope.launch {
                val newPair = lastPairRepository.getPair().copy(second = event.to)
                if (event.to != state.value.from)
                    lastPairRepository.setPair(newPair)
                val rates = ratesRepository.loadLatestRates(rates = ratesParam, CurrencyHelper.getAllPairs(), key = key)
                if (rates != null) {
                    ratesRepository.cacheLatestRates(rates)
                    setState { copy(to =  event.to, currentRates = rates) }
                    setEffect { Converter.Effect.ConnectionRestored }
                } else {
                    setState { copy(to =  event.to) }
                    setEffect { Converter.Effect.Offline }
                }
            }
            is Converter.Event.SwipeDown -> viewModelScope.launch {
                setEffect { Converter.Effect.Refresh }
                val rates = ratesRepository.loadLatestRates(rates = ratesParam, CurrencyHelper.getAllPairs(), key = key)
                if (rates != null) {
                    ratesRepository.cacheLatestRates(rates)
                    setState { copy(currentRates = rates) }
                    setEffect { Converter.Effect.ConnectionRestored }
                } else {
                    val cached = ratesRepository.getCachedRates()
                    if (cached != null) {
                        setState { copy(currentRates = cached) }
                        setEffect { Converter.Effect.Offline }
                    } else
                        setEffect { Converter.Effect.Error }
                }
            }
        }
    }
}