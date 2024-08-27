package dev.kissed.randomizer.app

import androidx.compose.runtime.Composable
import dev.kissed.common.architecture.BaseFeatureUI
import dev.kissed.randomizer.features.app.api.AppFeature
import dev.kissed.randomizer.features.main.api.ui.MainFeatureUI

@Composable
fun App(feature: AppFeature) {
    BaseFeatureUI(feature.main()) { state, dispatch ->
        MainFeatureUI(state, dispatch)    
    }
}