package ru.dbuzin.dev.sbertestapp.ui.base

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

const val EFFECT_LISTENER = "EFFECT_LISTENER"

abstract class BaseViewModel<State, Event, Effect>(initialState: State) : ViewModel() {
    private val _state = mutableStateOf(initialState)
    val state: androidx.compose.runtime.State<State> get() = _state

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    private val _event = MutableSharedFlow<Event>()

    init {
        viewModelScope.launch { _event.collect(::handleEvents) }
    }

    protected fun setState(reducer: State.() -> State) {
        _state.value = state.value.reducer()
    }


    fun setEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    abstract fun handleEvents(event: Event)

    protected fun setEffect(builder: () -> Effect) {
        viewModelScope.launch { _effect.send(builder()) }
    }
}