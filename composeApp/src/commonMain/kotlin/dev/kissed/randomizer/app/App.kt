package dev.kissed.randomizer.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.kissed.randomizer.model.Member
import dev.kissed.randomizer.pages.FortuneWheel
import dev.kissed.utils.nextColor
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

private data class ItemsModel(
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

private fun parseMembers(str: String): ItemsModel {
    val parsed = str.split("\n")
        .map { it.trim() }
        .filterNot { it.isBlank() }
    val itemsList = parsed.mapIndexed { idx, item ->
        Member(id = idx, name = item, color = Random.nextColor())
    }

    return ItemsModel(
        items = itemsList.associateBy { it.id },
        itemsShown = itemsList.map { it.id }.toSet(),
        order = itemsList.indices.shuffled(),
        currentPos = null,
    )
}

private val INITIAL = "Денис\nЕгор\nВаня С.\nВаня М.\nЖеня\nЭмиль"
private val INITIAL_MODEL = parseMembers(INITIAL)

@Composable
@Preview
fun App() {
    var model by remember { mutableStateOf(INITIAL_MODEL) }
    
    Column(
        Modifier.fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Колесо закрутится - ситдаун замутится",
            Modifier
                .padding(top = 50.dp),
            fontSize = 20.sp,
        )
        Button(
            onClick = {
                val currentPos = model.currentPos
                model = when {
                    model.itemsShown.size <= 1 -> return@Button
                    currentPos == null -> {
                        model.copy(currentPos = 0)
                    }

                    else -> {
                        model.copy(
                            itemsShown = model.itemsShown.filterNot { it == model.currentId }.toSet(),
                            currentPos = currentPos + 1,
                            currentChosen = false,
                        )
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
        ) {
            Text("Next")
        }
        Box() {
            FortuneWheel(
                model.items.filter { it.key in model.itemsShown }.values.toList(),
                model.currentId,
                onRotationFinished = { model = model.copy(currentChosen = true) }
            )
        }

        Text("Input:", fontWeight = FontWeight.ExtraBold)
        var itemsFieldState by remember { mutableStateOf(INITIAL) }
        TextField(
            value = itemsFieldState,
            onValueChange = {
                itemsFieldState = it
                model = parseMembers(it)
            },
        )

        Text("Output:", fontWeight = FontWeight.ExtraBold)
        Column(
            modifier = Modifier.widthIn(min = 100.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            model.chosen.forEachIndexed { idx, item ->
                Text("$idx: ${item.name}")
            }
        }
    }
}