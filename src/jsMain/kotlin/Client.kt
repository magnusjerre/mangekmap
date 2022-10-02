import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import react.create
import react.dom.client.createRoot

// Settes opp for at vi skal kunne kjøre asynkrone kall
val mainScope = MainScope()

fun main() {
    val container = document.createElement("div")
    document.body!!.appendChild(container)

    val app = App.create()
    createRoot(container).render(app)
}