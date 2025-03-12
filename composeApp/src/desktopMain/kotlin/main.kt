import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import dev.kissed.randomizer.app.App
import dev.kissed.randomizer.app.di.AppComponent
import dev.kissed.randomizer.app.di.create

fun main() = application {
    val state: WindowState by remember {
        mutableStateOf(
            WindowState(
                position = WindowPosition(Alignment.Center),
                size = DpSize(1000.dp, 1000.dp),
            )
        )
    }

    val appComponent = AppComponent::class.create()
    
    Window(
        state = state,
        onCloseRequest = ::exitApplication,
        title = "randomizer",
    ) {
        App(appComponent.appFeature())
    }
}