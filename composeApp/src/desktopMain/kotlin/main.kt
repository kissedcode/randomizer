import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.kissed.randomizer.app.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "randomizer",
    ) {
        App()
    }
}