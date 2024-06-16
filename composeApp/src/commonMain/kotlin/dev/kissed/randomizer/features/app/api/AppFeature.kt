package dev.kissed.randomizer.features.app.api

import dev.kissed.common.architecture.BaseFeature
import dev.kissed.randomizer.features.app.impl.AppFeatureImpl
import dev.kissed.randomizer.features.main.api.MainFeature

interface AppFeature : BaseFeature<Unit, Unit> {
    
    fun main(): MainFeature
    
    companion object {
        fun createImpl(): AppFeature {
            return AppFeatureImpl()
        }
    }
}