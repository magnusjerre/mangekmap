package events

import csstype.Display
import csstype.em
import csstype.px
import dto.EventPostDto
import kotlinx.coroutines.launch
import kotlinx.js.timers.setTimeout
import mainScope
import mui.material.Alert
import mui.material.AlertColor
import mui.material.AlertVariant
import mui.material.Box
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.Stack
import mui.system.sx
import react.FC
import react.Props
import react.router.dom.Link
import react.router.useNavigate
import react.router.useParams
import react.useEffectOnce
import react.useState

val EventDelete = FC<Props> {
    val params = useParams()
    var seasonId by useState<Long?>(null)
    var eventId by useState(params["eventId"]?.toLong())
    var event by useState(EventPostDto(date = "", title = "", categoryId = 0, venue = "", isTeamBased = false))
    var showSuccess by useState(false)
    var successfullyDeleted by useState(false)
    var loading by useState(false)
    val navigate = useNavigate()

    useEffectOnce {
        mainScope.launch {
            val eventDto = eventId?.let { getEvent(it) }
            event = eventDto?.let {
                EventPostDto(
                    date = it.date,
                    title = it.title,
                    categoryId = it.category.id,
                    venue = it.venue,
                    isTeamBased = it.isTeamBased
                )
            } ?: EventPostDto(date = "", title = "", categoryId = 1, venue = "", isTeamBased = false)
            if (eventDto != null) {
                seasonId = eventDto.seasonId
            }
        }
    }

    if (eventId == null) {
        Alert {
            variant = AlertVariant.filled
            color = AlertColor.error
            +"Må oppgi eventId"
        }
        return@FC
    }

    Box {
        sx {
            width = 400.px
        }

        Typography {
            variant = TypographyVariant.h1
            +event.title
        }

        Stack {
            asDynamic().spacing = 3

            Link {
                to = if (!successfullyDeleted) {
                    "/events/$eventId"
                } else {
                    "/seasons/$seasonId"
                }
                if (!successfullyDeleted) {
                    +"Tilbake til øvelse"
                } else {
                    +"Tilbake til sesong"
                }
            }


            if (showSuccess) {
                Alert {
                    variant = AlertVariant.outlined
                    severity = AlertColor.success
                    +"Endringer lagret"
                }
            }

            Box {
                sx {
                    display = Display.flex
                    marginTop = 1.em
                }

                Button {
                    disabled = loading
                    variant = ButtonVariant.contained
                    onClick = {
                        mainScope.launch {
                            loading = true
                            deleteEvent(eventId!!)
                            loading = false
                            showSuccess = true
                            successfullyDeleted = true

                            setTimeout({
                                showSuccess = false
                            }, 1500)
                        }
                    }
                    +"Slett øvelse"
                }

                if (!successfullyDeleted) {
                    Button {
                        variant = ButtonVariant.outlined
                        onClick = {
                            navigate("/events/$eventId")
                        }
                        +"Avbryt"
                    }
                }
            }
        }
    }
}