package io.github.gustavobarbosab.istore.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

interface MviDelegate<State, Effect> {
    val state: StateFlow<State>
    val sideEffect: SharedFlow<Effect>
    fun onState(state: State)
    suspend fun onSideEffect(effect: Effect)
}

interface MviEventHandler<Event> {
    fun onEvent(event: Event)
}

abstract class MviDelegateImpl<State, Effect>(
    initialState: State,
) : MviDelegate<State, Effect> {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<Effect>()
    override val sideEffect: SharedFlow<Effect> = _sideEffect.asSharedFlow()

    override fun onState(state: State) {
        _state.value = state
    }

    override suspend fun onSideEffect(effect: Effect) {
        _sideEffect.emit(effect)
    }
}