package dev.kissed.randomizer.app.di

import com.russhwolf.settings.Settings
import dev.kissed.randomizer.features.app.api.AppFeature
import dev.kissed.randomizer.features.app.impl.AppFeatureImpl
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope

@Scope
annotation class AppComponentScope

@Component
@AppComponentScope
internal abstract class AppComponent {

    abstract fun appFeature(): AppFeature

    val AppFeatureImpl.bind: AppFeature
        @Provides get() = this

    @Provides
    protected fun settings(): Settings {
        return Settings()
    }
}
