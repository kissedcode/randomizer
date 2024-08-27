package dev.kissed.common.architecture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun <STATE : Any, ACTION : Any> BaseFeatureUI(
    feature: BaseFeature<STATE, ACTION>,
    content: @Composable (state: STATE, dispatch: (action: ACTION) -> Unit) -> Unit
) {
    val state by feature.states.collectAsState()
    content(state, feature::dispatch)
}
