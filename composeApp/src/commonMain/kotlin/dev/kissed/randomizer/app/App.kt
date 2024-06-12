package dev.kissed.randomizer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.kissed.randomizer.features.main.api.MainFeature
import dev.kissed.randomizer.features.main.api.ui.MainFeatureUI

@Composable
fun App() {
    val feature: MainFeature = remember { MainFeature.createImpl() }
    MainFeatureUI(MainFeature.createImpl())
}