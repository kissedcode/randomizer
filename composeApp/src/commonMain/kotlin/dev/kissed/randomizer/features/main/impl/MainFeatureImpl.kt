package dev.kissed.randomizer.features.main.impl

import dev.kissed.common.architecture.BaseFeatureImpl
import dev.kissed.common.compose.nextColor
import dev.kissed.randomizer.features.main.api.MainFeature
import dev.kissed.randomizer.features.main.api.MainFeature.Action
import dev.kissed.randomizer.features.main.api.MainFeature.State
import dev.kissed.randomizer.features.main.impl.data.InputRepository
import dev.kissed.randomizer.model.Member
import kotlin.random.Random

internal class MainFeatureImpl(
    private val inputRepository: InputRepository,
) : MainFeature, BaseFeatureImpl<State, Action>(
    initialState = run {
        val initialInput = inputRepository.get() ?: INITIAL_INPUT
        val itemsList = parse(initialInput)
        State(
            input = initialInput,
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
            Action.Next -> {
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
                if (action.text.contains(CHEATCODE)) {
                    changeInput(ALPHA_TEAM_HARDCODE)
                } else {
                    changeInput(action.text)
                }
            }
        }
    }

    private fun changeInput(newInput: String) {
        inputRepository.save(newInput)
        val newItems = parse(newInput)
        state = state.copy(
            input = newInput,
            itemsList = newItems,
            order = newItems.indices.shuffled(),
            currentPos = null,
            currentChosen = false,
            itemsHidden = emptySet(),
        )
    }

    companion object {

        private val INITIAL_INPUT = (1..10).joinToString(separator = "\n") { it.toString() }

        private val CHEATCODE = "iddqd"
        
        private val ALPHA_TEAM_HARDCODE = """
            Денис
            Женя
            Ваня С.
            Ваня М.
            Эмиль
            Егор
            Бахтиёр
        """.trimIndent()
        
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

