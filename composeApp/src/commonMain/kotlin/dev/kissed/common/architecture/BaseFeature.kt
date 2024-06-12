package dev.kissed.common.architecture

import kotlinx.coroutines.flow.StateFlow

interface BaseFeature<STATE : Any, ACTION : Any> {

    val states: StateFlow<STATE>

    fun dispatch(action: ACTION)
}