package ru.dbuzin.dev.sbertestapp.ui.feature.converter

import ru.dbuzin.dev.sbertestapp.domain.model.LatestRates

object Converter {
    data class State(
        val from: String = "USD",
        val to: String = "RUB",
        val currentRates: LatestRates? = null,
    )

    sealed class Event {
        data class ValueFromChanged(val from: String): Event()
        data class ValueToChanged(val to: String): Event()
        object SwipeDown: Event()
    }

    sealed class Effect {
        object Refresh: Effect()
        object Offline: Effect()
        object ConnectionRestored: Effect()
        object Error: Effect()
    }
}