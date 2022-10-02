import persons.PersonEdit
import react.FC
import react.Props
import react.createElement
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ul
import react.router.Route
import react.router.Routes
import react.router.dom.HashRouter
import react.router.dom.Link

val App = FC<Props> {
    // https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-react-router-dom/README.md
    HashRouter {
        div {
            ul {
                li {
                    Link {
                        to = "/"
                        +"Hjem"
                    }
                }
                li {
                    Link {
                        to = "/persons"
                        +"Personer"
                    }
                }
                li {
                    Link {
                        to = "/persons/new"
                        +"Ny person"
                    }
                }
            }
        }
        Routes {
            Route {
                path = "/persons/new"
                element = createElement(PersonEdit)
            }
            Route {
                path = "/persons/:id"
                element = createElement(PersonEdit)
            }
            Route {
                path = "/persons"
                element = createElement(Persons)
            }
            Route {
                path = "/"
                element = createElement(Welcome)
            }
        }
    }
}
