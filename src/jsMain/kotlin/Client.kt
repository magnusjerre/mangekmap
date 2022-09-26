import kotlinx.browser.document
import org.w3c.dom.ElementCreationOptions
import react.create
import react.dom.client.createRoot

fun main() {
    val container = document.createElement("div")
    document.body!!.appendChild(container)

    val welcome = Welcome.create {
        name = "Kotlin/JSfffff"
    }
    createRoot(container).render(welcome)
}