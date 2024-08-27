import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.kissed.randomizer.app.App
import dev.kissed.randomizer.app.di.AppComponent
import dev.kissed.randomizer.app.di.create
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val appComponent = AppComponent::class.create()
    val appFeature = appComponent.appFeature()
    
    ComposeViewport(document.body!!) {
        App(appFeature)
    }
}