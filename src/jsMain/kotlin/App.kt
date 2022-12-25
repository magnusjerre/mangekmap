import csstype.number
import events.EventAddParticipants
import events.EventDelete
import events.EventEdit
import events.EventEditResults
import events.EventOverview
import kotlinx.js.jso
import login.ChangePassword
import login.Login
import mui.material.Alert
import mui.material.AlertColor
import mui.material.AlertVariant
import mui.material.Snackbar
import mui.material.SnackbarOriginHorizontal
import mui.material.SnackbarOriginVertical
import mui.material.Typography
import person_events.PersonEvents
import persons.PersonEdit
import react.FC
import react.Props
import react.createElement
import react.router.Route
import react.router.Routes
import react.router.dom.HashRouter
import react.useState
import seasons.Season
import seasons.SeasonEdit
import seasons.Seasons

open class AppAlertProps(val variant: AlertVariant, val severity: AlertColor, val text: String, val duration: Long = 2000) {
    companion object {
        fun success(text: String) = AppAlertProps(AlertVariant.outlined, AlertColor.success, text)
    }
}

external interface AppAlertUsageProps : Props {
    var handleAlert: (AppAlertProps) -> Unit
}

val App = FC<Props> {
    var showAlert by useState(false)
    var alertProps by useState<AppAlertProps>(jso {})
    val alertUsageProps = jso<AppAlertUsageProps> {
        handleAlert = {
            alertProps = it
            showAlert = true
        }
    }

    Snackbar {
        anchorOrigin = jso {
            vertical = SnackbarOriginVertical.top
            horizontal = SnackbarOriginHorizontal.center
        }
        open = showAlert
        autoHideDuration = alertProps.duration?.toDouble()
        onClose = { _,_ ->  showAlert = false }
        Alert {
            variant = alertProps.variant
            severity = alertProps.severity
            +alertProps.text
        }
    }

    Typography {
        // https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-react-router-dom/README.md
        HashRouter {
            Routes {
                Route {
                    path = "/persons/new"
                    element = createElement(PersonEdit, alertUsageProps)
                }
                Route {
                    path = "/persons/:id"
                    element = createElement(PersonEdit, alertUsageProps)
                }
                Route {
                    path = "/persons/:personId/participations"
                    element = createElement(PersonEvents)
                }
                Route {
                    path = "/persons"
                    element = createElement(Persons)
                }
                Route {
                    path = "/seasons/:id/edit"
                    element = createElement(SeasonEdit, alertUsageProps)
                }
                Route {
                    path = "/seasons/new"
                    element = createElement(SeasonEdit, alertUsageProps)
                }
                Route {
                    path = "/seasons/:id"
                    element = createElement(Season)
                }
                Route {
                    path = "/seasons/:seasonId/events"
                    element = createElement(EventEdit, alertUsageProps)
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
                    element = createElement(EventEdit, alertUsageProps)
                }
                Route {
                    path = "/events/:eventId/delete"
                    element = createElement(EventDelete, alertUsageProps)
                }
                Route {
                    path = "/events/:id/participants/addremove"
                    element = createElement(EventAddParticipants, alertUsageProps)
                }
                Route {
                    path = "/events/:id/participants/editresults"
                    element = createElement(EventEditResults, alertUsageProps)
                }
                Route {
                    path = "/custom/login"
                    element = createElement(Login)
                }
                Route {
                    path = "/admin/changepassword"
                    element = createElement(ChangePassword)
                }
                Route {
                    path = "/"
                    element = createElement(Welcome)
                }
            }
        }
    }
}
