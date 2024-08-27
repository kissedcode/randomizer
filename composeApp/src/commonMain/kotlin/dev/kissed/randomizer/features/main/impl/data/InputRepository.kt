package dev.kissed.randomizer.features.main.impl.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import dev.kissed.randomizer.app.di.AppComponentScope
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Scope

@Scope
annotation class InputRepositoryScope

@AppComponentScope
class InputRepository @Inject constructor(
    private val settings: Settings,
) {

    fun save(input: String) {
        settings.putString(KEY_INPUT, input)
    }

    fun get(): String? {
        return settings[KEY_INPUT]
    }

    companion object {
        private const val KEY_INPUT = "input"
    }
}