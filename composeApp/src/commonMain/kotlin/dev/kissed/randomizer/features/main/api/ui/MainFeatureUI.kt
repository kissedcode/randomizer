package dev.kissed.randomizer.features.main.api.ui

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
import androidx.compose.runtime.collectAsState
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
import dev.kissed.randomizer.features.main.api.MainFeature
import dev.kissed.randomizer.features.main.impl.ui.FortuneWheel

@Composable
fun MainFeatureUI(feature: MainFeature) {
    val state by feature.states.collectAsState()

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
                feature.dispatch(MainFeature.Action.NextClick)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
        ) {
            Text("Next")
        }
        Box() {
            FortuneWheel(
                state.itemsList.filterNot { it.id in state.itemsHidden },
                state.currentId,
                onRotationFinished = {
                    feature.dispatch(MainFeature.Action.RotationFinished)
                }
            )
        }

        Text("Input:", fontWeight = FontWeight.ExtraBold)
        var itemsFieldState by remember { mutableStateOf(state.itemsList.map { it.name }.joinToString(separator = "\n")) }
        TextField(
            value = itemsFieldState,
            onValueChange = {
                itemsFieldState = it
                feature.dispatch(MainFeature.Action.InputChanged(it))
            },
        )

        Text("Output:", fontWeight = FontWeight.ExtraBold)
        Column(
            modifier = Modifier.widthIn(min = 100.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            state.chosen.forEachIndexed { idx, item ->
                Text("$idx: ${item.name}")
            }
        }
    }
}