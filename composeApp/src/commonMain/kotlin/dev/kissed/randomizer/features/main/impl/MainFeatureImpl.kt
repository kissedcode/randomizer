package dev.kissed.randomizer.features.main.impl

import dev.kissed.common.architecture.BaseFeatureImpl
import dev.kissed.common.compose.nextColor
import dev.kissed.randomizer.features.main.api.MainFeature
import dev.kissed.randomizer.features.main.api.MainFeature.Action
import dev.kissed.randomizer.features.main.api.MainFeature.State
import dev.kissed.randomizer.model.Member
import kotlin.random.Random

internal class MainFeatureImpl : MainFeature, BaseFeatureImpl<State, Action>(
    initialState = run {
        val itemsList = parse(INITIAL_TEXT)
        State(
            itemsList = itemsList,
            order = itemsList.indices.shuffled(),
            currentPos = null,
            currentChosen = false,
            itemsHidden = emptySet(),
            page = MainFeature.Page.WHEEL,
        )    
    }
) {
    override fun dispatch(action: Action) {
        when (action) {
            Action.NextClick -> {
                val currentPos = state.currentPos
                state = when {
                    state.itemsHidden.size == state.itemsList.size - 1 -> state
                    currentPos == null -> {
                        state.copy(currentPos = 0)
                    }

                    else -> {
                        state.copy(
                            currentPos = currentPos + 1,
                            currentChosen = false,
                            itemsHidden = state.currentId!!.let { state.itemsHidden + it }
                        )
                    }
                }
            }

            Action.NextAnimationFinished -> {
                state = state.copy(currentChosen = true)
            }

            is Action.InputChanged -> {
                val itemsList = parse(action.text)
                state = state.copy(
                    itemsList = itemsList,
                    order = itemsList.indices.shuffled(),
                    currentPos = null,
                    currentChosen = false,
                    itemsHidden = emptySet(),
                )
            }
        }
    }

    companion object {

        private const val INITIAL_TEXT = "Денис\nЕгор\nВаня С.\nВаня М.\nЖеня\nЭмиль"
        private fun parse(text: String): List<Member> {
            val parsed = text.split("\n")
                .map { it.trim() }
                .filterNot { it.isBlank() }

            val itemsList = parsed.mapIndexed { idx, item ->
                Member(id = idx, name = item, color = Random.nextColor())
            }
            return itemsList
        }
    }
}