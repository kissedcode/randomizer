package dev.kissed.randomizer.features.app.impl

import dev.kissed.common.architecture.BaseFeatureImpl
import dev.kissed.randomizer.app.di.AppComponentScope
import dev.kissed.randomizer.features.app.api.AppFeature
import dev.kissed.randomizer.features.main.api.MainFeature
import dev.kissed.randomizer.features.main.impl.data.InputRepository
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Scope

@Scope
annotation class AppFeatureScope

@AppComponentScope
internal class AppFeatureImpl @Inject constructor(
    inputRepository: InputRepository,
) : AppFeature, BaseFeatureImpl<Unit, Unit>(Unit) {

    private val main: MainFeature by lazy { MainFeature.createImpl(inputRepository) }
    
    override fun main(): MainFeature {
        return main
    }
}