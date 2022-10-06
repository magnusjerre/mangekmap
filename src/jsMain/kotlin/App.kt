import events.EventAddParticipants
import events.EventOverview
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
import seasons.Season
import seasons.SeasonEdit
import seasons.Seasons

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
                li {
                    Link {
                        to = "/seasons/new"
                        +"Ny sesong"
                    }
                }
                li {
                    Link {
                        to = "/seasons"
                        +"Sesonger"
                    }
                }
                li {
                    Link {
                        to = "/events/4"
                        +"Event 4"
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
                path = "/seasons/:id/edit"
                element = createElement(SeasonEdit)
            }
            Route {
                path = "/seasons/new"
                element = createElement(SeasonEdit)
            }
            Route {
                path = "/seasons/:id"
                element = createElement(Season)
            }
            Route {
                path = "/seasons"
                element = createElement(Seasons)
            }
            Route {
                path = "/events/:id"
                element = createElement(EventOverview)
            }
            Route {
                path = "/events/:id/participants/addremove"
                element = createElement(EventAddParticipants)
            }
            Route {
                path = "/"
                element = createElement(Welcome)
            }
        }
    }
}
