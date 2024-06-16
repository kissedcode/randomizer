package dev.kissed.randomizer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.kissed.randomizer.features.app.api.AppFeature
import dev.kissed.randomizer.features.main.api.MainFeature
import dev.kissed.randomizer.features.main.api.ui.MainFeatureUI

@Composable
fun App(feature: AppFeature) {
    MainFeatureUI(feature.main())
}