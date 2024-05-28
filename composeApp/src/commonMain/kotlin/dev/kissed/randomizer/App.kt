package dev.kissed.randomizer

import Greeting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import randomizer.composeapp.generated.resources.Res
import randomizer.composeapp.generated.resources.compose_multiplatform
import kotlin.random.Random

private data class Member(
    val id: Int,
    val name: String,
    val color: Color,
)

private val members = listOf("Денис", "Егор", "Ваня С.", "Ваня М.", "Женя", "Эмиль")
    .shuffled()
    .mapIndexed { idx, name ->
        Member(
            id = idx,
            name = name,
            color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()),
        )
    }

@Composable
@Preview
fun App() {
    val scope = rememberCoroutineScope()
    val rotationXAnim = remember { Animatable(0f) }

    Box(
        Modifier.fillMaxSize()
            .background(Color.White)
            .clickable(
                remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    scope.launch {
                        rotationXAnim.animateDecay(6000f, exponentialDecay(frictionMultiplier = 0.5f))
                    }
                    scope.launch {
                        delay(Random.nextLong(300, 2000))
                        rotationXAnim.animateDecay(rotationXAnim.velocity, exponentialDecay(frictionMultiplier = 1.5f))
                    }
                }
            )
    ) {
        Box(
            Modifier
                .align(Alignment.Center)
                .size(300.dp)
                .graphicsLayer {
                    rotationZ = rotationXAnim.value
                }
                .drawBehind {
                    var angle = 0f
                    val angleStep = 360f / members.size
                    members.forEach {
                        drawArc(color = it.color, startAngle = angle, sweepAngle = angleStep, useCenter = true)
                        angle += angleStep
                    }
                }
        ) {
            var angle = 20f
            val angleStep = 360f / members.size
            members.forEach {
                Box(
                    Modifier.matchParentSize().rotate(angle)
                ) {
                    Text(
                        it.name,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 10.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
                angle += angleStep
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
}