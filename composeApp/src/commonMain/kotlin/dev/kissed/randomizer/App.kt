package dev.kissed.randomizer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

private data class Member(
    val id: Int,
    val name: String,
    val color: Color,
)

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
)

@Composable
@Preview
fun App() {
    var model by remember {
        mutableStateOf(ItemsModel(members, order = null))
    }
    Box(
        Modifier.fillMaxSize()
            .background(Color.White)
            .clickable(
                remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    when {
                        model.items.size <= 1 -> return@clickable
                        model.order == null -> {
                            model = model.copy(order = model.items.indices.toMutableList().shuffled())
                        }

                        else -> {
                            val currentIdx = model.order!!.first()
                            model = model.copy(
                                items = model.items.filterNot { it.id == currentIdx },
                                order = model.order!!.drop(1)
                            )
                        }
                    }
                }
            )
    ) {
        FortuneWheel(model.items, model.order?.first())    
    }
}

private data class WheelModel(
    val items: List<Member>,
    val currentId: Int?,
) {
    val angleStep: Float = 360f / items.size
    val angles: List<Pair<Float, Float>> by lazy {
        items.indices.map {
            it * angleStep to (it + 1) * angleStep
        }
    }
    val currentIdx: Int? = currentId?.let { items.indexOfFirst { it.id == currentId } }
}

@Composable
private fun BoxScope.FortuneWheel(items: List<Member>, currentId: Int?) {
    val wheelModel = remember(currentId) { WheelModel(items, currentId) }
    val rotationXAnim = remember { Animatable(0f) }

    LaunchedEffect(currentId) {
        wheelModel.currentIdx ?: return@LaunchedEffect
        val targetAngle = -wheelModel.angles[wheelModel.currentIdx].let { (it.first + it.second)/2 }
        rotationXAnim.animateTo(
            targetAngle + 360 * (members.size - items.size + 1) * 5,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessLow,
            )
        )
    }

    Box(
        Modifier
            .align(Alignment.Center)
            .size(300.dp)
            .graphicsLayer {
                rotationZ = rotationXAnim.value
            }
            .drawBehind {
                wheelModel.items.forEachIndexed { idx, member ->
                    drawArc(
                        color = member.color,
                        startAngle = wheelModel.angles[idx].first,
                        sweepAngle = wheelModel.angleStep,
                        useCenter = true
                    )
                }
            }
    ) {
        wheelModel.items.forEachIndexed { idx, member ->
            Box(
                Modifier.matchParentSize().rotate(wheelModel.angles[idx].let { (it.first + it.second)/2 })
            ) {
                Text(
                    member.name,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 30.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }

    Image(
        Icons.Default.PlayArrow,
        modifier = Modifier
            .size(50.dp)
            .rotate(180f)
            .align(Alignment.Center)
            .offset(x = (-150).dp),
        contentDescription = null,
    )
}