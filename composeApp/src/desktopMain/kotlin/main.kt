import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.kissed.randomizer.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "randomizer",
    ) {
        App()
    }
}