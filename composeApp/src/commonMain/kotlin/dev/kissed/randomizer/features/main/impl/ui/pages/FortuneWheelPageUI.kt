package dev.kissed.randomizer.features.main.impl.ui.pages

import androidx.compose.animation.SplineBasedFloatDecayAnimationSpec
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.kissed.common.util.toFloat
import dev.kissed.randomizer.features.main.impl.ui.pages.Line.Companion.intersection
import dev.kissed.randomizer.model.Member
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import randomizer.composeapp.generated.resources.Res
import randomizer.composeapp.generated.resources.spiral
import kotlin.math.PI
import kotlin.math.acos
import kotlin.random.Random

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
internal fun BoxScope.FortuneWheelPageUI(items: List<Member>, currentId: Int?, onNextAniationFinished: () -> Unit) {
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

            onNextAniationFinished()
        }
    }

    val radius = 150.dp
    val diameter = radius * 2

    Box(
        Modifier
            .align(Alignment.Center)
            .size(diameter)
    ) {
        Box(
            Modifier
                .align(Alignment.Center)
                .size(diameter)
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
        ) {
            Image(
                painterResource(Res.drawable.spiral), null,
                modifier = Modifier.align(Alignment.Center).size(50.dp),
                colorFilter = ColorFilter.tint(Color.White),
            )
            wheelModel.items.forEachIndexed { idx, member ->
                Box(
                    Modifier.matchParentSize().rotate(wheelModel.angles[idx].let { (it.first + it.second) / 2 })
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

        Box(
            Modifier
                .matchParentSize()
                .pointerInput(currentId) {
                    val center = Offset(x = size.width / 2f, y = size.height / 2f)
                    val velocityTracker = VelocityTracker()

                    awaitEachGesture {
                        val down = awaitFirstDown()
                        var clockwise = true

                        velocityTracker.resetTracking()

                        drag(down.id) { drag ->
                            val a = drag.previousPosition
                            val b = drag.position
                            
                            val lineOA = Line.fromThroughTwoPoints(center, a)
                            val lineAC = lineOA?.let { Line.fromOrtogonalLineThroughPoint(lineOA, a) }
                            val lineBC = lineAC?.let { Line.fromOrtogonalLineThroughPoint(it, b) }
                            
                            val c = if (lineAC != null && lineBC != null) lineAC.intersection(lineBC) else null

                            c?.let { 
                                velocityTracker.addPosition(drag.uptimeMillis, c)
                                
                                val aNorm = a - center
                                val cNorm = c - center
                                val quarterTop = (aNorm.y < 0)
                                val directionRight = aNorm.x < cNorm.x
                                clockwise = quarterTop xor directionRight
                                
                                val rotation = run { 
                                    val oa = (center - a).getDistance().takeUnless { it == 0f } ?: return@run 0f
                                    val ob = (center - b).getDistance().takeUnless { it == 0f } ?: return@run 0f
                                    val ab = (a - b).getDistance()
                                    acos((oa * oa + ob * ob - ab * ab) / (2 * oa * ob)) / PI.toFloat() * 180f
                                }
                                
                                scope.launch {
                                    if (rotationStarted) onNextAniationFinished()
                                    rotationAnim.snapTo(rotationAnim.value + rotation * (!clockwise).toFloat())
                                }
                            }
                        }

                        val velocity = velocityTracker.calculateVelocity()
                        scope.launch {
                            if (rotationStarted) onNextAniationFinished()
                            rotationAnim.animateDecay(velocity.abs() * (!clockwise).toFloat(), exponentialDecay(0.9f))
                        }
                    }
                }
        )
    }

    Image(
        Icons.Default.PlayArrow,
        modifier = Modifier
            .size(50.dp)
            .rotate(180f)
            .align(Alignment.Center)
            .offset(x = -radius),
        contentDescription = null,
        colorFilter = ColorFilter.lighting(Color.Black, Color.Black)
    )
}

private fun Line.debugDots(center: Offset, radiusPx: Int = 200): List<Offset> {
    return (0..100).mapNotNull {
        val x = Random.nextInt(center.x.toInt() - radiusPx, center.x.toInt() + radiusPx).toFloat()
        val y = this.y(x)
        y?.let { Offset(x, y) }
    }
}

fun Velocity.abs(): Float {
    return Offset(x, y).getDistance()
}

/**
 * ax + by + c = 0
 * a, b, c are normalized
 */
class Line(a: Float, b: Float, c: Float) {
    val a: Float
    val b: Float
    val c: Float

    init {
        when {
            a != 0f -> {
                this.a = 1f
                this.b = b / a
                this.c = c / a
            }

            b != 0f -> {
                this.a = a / b
                this.b = 1f
                this.c = c / b
            }

            else -> {
                this.a = 0f
                this.b = 0f
                this.c = 0f
            }
        }
    }

    fun x(y: Float): Float? {
        val x = if (a != 0f) (-c - b * y) / a else null
        return x
    }

    fun y(x: Float): Float? {
        val y = if (b != 0f) (-c - a * x) / b else null
        return y
    }

    companion object {
        fun fromThroughTwoPoints(p0: Offset, p1: Offset): Line? {
            val line = when {
                p0 == p1 -> {
                    null
                }

                p0.x == p1.x -> {
                    Line(a = 1f, b = 0f, c = -p0.x)
                }

                p0.y == p1.y -> {
                    Line(a = 0f, b = 1f, c = -p0.y)
                }

                else -> {
                    val b = (p1.x - p0.x) / (p0.y - p1.y)
                    Line(
                        a = 1f,
                        b = b,
                        c = -p0.x - b * p0.y
                    )
                }
            }
            return line
        }

        fun fromOrtogonalLineThroughPoint(orthogonal: Line, p: Offset): Line {
            val line = when {
                orthogonal.a == 0f -> {
                    Line(a = 1f, b = 0f, c = -p.x)
                }

                orthogonal.b == 0f -> {
                    Line(a = 0f, b = 1f, c = -p.y)
                }

                else -> {
                    val a = -orthogonal.b / orthogonal.a
                    Line(
                        a = a,
                        b = 1f,
                        c = -a * p.x - p.y,
                    )
                }
            }
            return line
        }

        fun Line.intersection(other: Line): Offset? {
            val point = when {
                a == 0f && b == 0f -> {
                    null
                }

                a == 0f -> {
                    val y = -c / b
                    val x = other.x(y)
                    x?.let { Offset(x, y) }
                }

                b == 0f -> {
                    val x = -c / a
                    val y = other.y(x)
                    y?.let { Offset(x, y) }
                }

                other.a == 0f && other.b == 0f -> {
                    null
                }

                other.a == 0f -> {
                    val y = -other.c / other.b
                    val x = this.x(y)
                    x?.let { Offset(x, y) }
                }

                other.b == 0f -> {
                    val x = -other.c / other.a
                    val y = this.y(x)
                    y?.let { Offset(x, y) }
                }

                // parallel
                b == other.b -> {
                    null
                }
                
                else -> {
                    val y = -(c - other.c) / (b - other.b)
                    val x = -c - b * y
                    Offset(x, y)
                }
            }
            return point
        }
    }
}