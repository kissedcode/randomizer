package dev.kissed.randomizer.features.main.impl.ui.pages

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.kissed.randomizer.model.Member
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private fun Float.whole(): Float {
    return roundToInt().toFloat()
}

@Composable
internal fun BoxScope.SimplePageUI(items: List<Member>, currentIdx: Int?, onNextAniationFinished: () -> Unit) {
    val scope = rememberCoroutineScope()
    val animatable = remember {
        Animatable(0f).also {
        }
    }
    val shownIdx by derivedStateOf {
        currentIdx?.let {
            (animatable.value.whole().mod(items.size.toDouble())).toInt()
        }
    }
    val shownItem by derivedStateOf {
        val idx = shownIdx 
        idx?.let { items[it] }
    }

    LaunchedEffect(currentIdx) {
        currentIdx ?: return@LaunchedEffect
        scope.launch { 
            animatable.animateTo(
                currentIdx + animatable.value - (animatable.value.toInt().mod(items.size)) + items.size * 40,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                    visibilityThreshold = 1f,
                )
            )
            onNextAniationFinished()
        }
    }

    Text(
        shownItem?.name ?: "---------",
        modifier = Modifier.align(Alignment.Center),
        color = shownItem?.color ?: Color.Black,
        fontWeight = FontWeight.Bold,
        fontSize = 70.sp,
    )
}