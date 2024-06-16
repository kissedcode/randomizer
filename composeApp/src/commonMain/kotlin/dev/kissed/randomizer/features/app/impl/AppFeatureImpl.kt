package dev.kissed.randomizer.features.app.impl

import dev.kissed.common.architecture.BaseFeatureImpl
import dev.kissed.randomizer.features.app.api.AppFeature
import dev.kissed.randomizer.features.main.api.MainFeature

internal class AppFeatureImpl : AppFeature, BaseFeatureImpl<Unit, Unit>(Unit) {

    private val main: MainFeature by lazy { MainFeature.createImpl() }
    
    override fun main(): MainFeature {
        return main
    }
}