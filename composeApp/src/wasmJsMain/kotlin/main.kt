import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.kissed.randomizer.app.App
import dev.kissed.randomizer.features.app.api.AppFeature
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val appFeature = AppFeature.createImpl()
    ComposeViewport(document.body!!) {
        App(appFeature)
    }
}