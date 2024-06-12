package dev.kissed.randomizer.features.main.impl

import dev.kissed.randomizer.model.Member

data class ItemsModel(
    val items: Map<Int, Member>,
    val itemsShown: Set<Int>,
    val order: List<Int>,
    val currentPos: Int?,
    val currentChosen: Boolean = false,
) {
    val currentId: Int? = currentPos?.let { order[currentPos] }
    val current: Member? = currentId?.let { items[currentId] }
    val chosen: List<Member> = currentPos?.let {
        order.take(if (currentChosen) currentPos + 1 else currentPos).map { items[it]!! }
    } ?: emptyList()
}