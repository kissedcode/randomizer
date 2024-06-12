package dev.kissed.randomizer.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.kissed.randomizer.model.Member
import dev.kissed.randomizer.pages.FortuneWheel
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

private val members = listOf("Денис", "Егор", "Ваня С.", "Ваня М.", "Женя", "Эмиль")
    .mapIndexed { idx, name ->
        Member(
            id = idx,
            name = name,
            color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()),
        )
    }

private data class ItemsModel(
    val items: List<Member>,
    val order: List<Int>?,
) {
    val currentId: Int? = order?.first()
    val current: Member? = currentId?.let { items.first { it.id == currentId } }
}

@Composable
@Preview
fun App() {
    var model by remember { mutableStateOf(ItemsModel(members, order = null)) }
    var chosen by remember { mutableStateOf<List<Member>>(emptyList()) }
    
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
                model = when {
                    model.items.size <= 1 -> return@Button
                    model.order == null -> {
                        model.copy(order = model.items.indices.toMutableList().shuffled())
                    }

                    else -> {
                        chosen = (model.current?.let { listOf(it) } ?: emptyList()) + chosen
                        val currentIdx = model.order!!.first()
                        model.copy(
                            items = model.items.filterNot { it.id == currentIdx },
                            order = model.order!!.drop(1)
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
                model.items,
                model.order?.first(),
            )
        }

//        Column {
//            chosen.forEach {
//                Text(it.name)
//            }
//        }
    }
}