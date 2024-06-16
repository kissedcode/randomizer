package dev.kissed.common.architecture

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseFeatureImpl<STATE : Any, ACTION : Any>(initialState: STATE) : BaseFeature<STATE, ACTION> {

    protected lateinit var scope: CoroutineScope

    private val _states = MutableStateFlow(initialState)
    protected var state: STATE
        get() = _states.value
        set(value) {
            _states.value = value
        }

    override val states: StateFlow<STATE> = _states

    fun start(scope: CoroutineScope) {
        this.scope = scope
        onStarted()
    }

    open fun onStarted() {}

    override fun dispatch(action: ACTION) {
    }
}