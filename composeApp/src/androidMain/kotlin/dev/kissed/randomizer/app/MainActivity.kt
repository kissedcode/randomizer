package dev.kissed.randomizer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.kissed.randomizer.app.App
import dev.kissed.randomizer.features.app.api.AppFeature

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appFeature = AppFeature.createImpl()
        
        setContent {
            App(appFeature)
        }
    }
}