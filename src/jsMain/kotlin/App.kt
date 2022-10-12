import events.EventAddParticipants
import events.EventEdit
import events.EventEditResults
import events.EventOverview
import persons.PersonEdit
import react.FC
import react.Props
import react.createElement
import react.router.Route
import react.router.Routes
import react.router.dom.HashRouter
import seasons.Season
import seasons.SeasonEdit
import seasons.Seasons

val App = FC<Props> {
    // https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-react-router-dom/README.md
    HashRouter {
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
                path = "/seasons/:seasonId/events"
                element = createElement(EventEdit)
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
                path = "/events/:eventId/edit"
                element = createElement(EventEdit)
            }
            Route {
                path = "/events/:id/participants/addremove"
                element = createElement(EventAddParticipants)
            }
            Route {
                path = "/events/:id/participants/editresults"
                element = createElement(EventEditResults)
            }
            Route {
                path = "/"
                element = createElement(Welcome)
            }
        }
    }
}
