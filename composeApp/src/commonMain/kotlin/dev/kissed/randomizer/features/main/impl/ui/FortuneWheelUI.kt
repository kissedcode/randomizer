package dev.kissed.randomizer.features.main.impl.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.kissed.randomizer.model.Member
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import randomizer.composeapp.generated.resources.Res
import randomizer.composeapp.generated.resources.spiral

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
internal fun BoxScope.FortuneWheel(items: List<Member>, currentId: Int?, onRotationFinished: () -> Unit) {
    val scope = rememberCoroutineScope()
    val wheelModel = remember(items, currentId) { WheelModel(items, currentId) }
    val rotationAnim = remember { Animatable(0f) }
    var rotationStarted by remember(currentId) { mutableStateOf(false) }

    LaunchedEffect(currentId) {
        wheelModel.currentIdx ?: return@LaunchedEffect
        
        val targetAngle = -wheelModel.angles[wheelModel.currentIdx].let { (it.first + it.second)/2 }

        rotationStarted = true
        scope.launch {
            rotationAnim.animateTo(
                targetAngle + rotationAnim.value - (rotationAnim.value.mod(360f)) + 360 * 50,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = 1f,
                )
            )

            onRotationFinished()
        }
    }

    Box(
        Modifier
            .align(Alignment.Center)
            .size(300.dp)
            .graphicsLayer {
                rotationZ = rotationAnim.value
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
            .pointerInput(currentId) {
                val midX = this.size.width / 2
                val velocityTracker = VelocityTracker()
                var direction: Float = 1f

                awaitEachGesture {
                    val down = awaitFirstDown()
                    velocityTracker.resetTracking()
                    drag(down.id) {
                        velocityTracker.addPosition(it.uptimeMillis, it.position)
                        scope.launch {
                            val delta = it.position - it.previousPosition
                            direction = (it.position.x > midX).let { if (it) +1f else -1f }
                            
                            if (rotationStarted) onRotationFinished()
                            rotationAnim.snapTo(rotationAnim.value + delta.y / 2 * direction)
                        }
                    }

                    val velocity = velocityTracker.calculateVelocity()
                    scope.launch {
                        if (rotationStarted) onRotationFinished()
                        rotationAnim.animateDecay(velocity.y * direction, exponentialDecay(0.5f))
                    }
                }
            }
    ) {
        Image(
            painterResource(Res.drawable.spiral), null,
            modifier = Modifier.align(Alignment.Center).size(50.dp),
            colorFilter = ColorFilter.tint(Color.White),
        )
        wheelModel.items.forEachIndexed { idx, member ->
            Box(
                Modifier.matchParentSize().rotate(wheelModel.angles[idx].let { (it.first + it.second)/2 })
            ) {
                val current = (idx == wheelModel.currentIdx)
                Text(
                    member.name,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 30.dp),
                    color = Color.White,
                    fontWeight = if (current) FontWeight.ExtraBold else FontWeight.Medium,
                    fontSize = if (current) 20.sp else 15.sp,
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
        colorFilter = ColorFilter.lighting(Color.Black, Color.Black)
    )
}