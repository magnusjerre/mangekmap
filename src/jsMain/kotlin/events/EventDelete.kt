package events

import AppAlertUsageProps
import components.ButtonTexts
import components.ModificationButtons
import components.OnHandler
import components.OnResult
import csstype.px
import dto.EventPostDto
import kotlinx.coroutines.launch
import mainScope
import mui.material.Alert
import mui.material.AlertColor
import mui.material.AlertVariant
import mui.material.Box
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.Stack
import mui.system.sx
import react.FC
import react.router.dom.Link
import react.router.useParams
import react.useEffectOnce
import react.useState

val EventDelete = FC<AppAlertUsageProps> { props ->
    val params = useParams()
    var seasonId by useState<Long?>(null)
    var eventId by useState(params["eventId"]?.toLong())
    var event by useState(EventPostDto(date = "", title = "", categoryId = 0, venue = "", isTeamBased = false))
    var successfullyDeleted by useState(false)

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
                to = "/seasons/$seasonId"
                +"Tilbake til sesong"
            }

            ModificationButtons {
                onSave = object : OnHandler {
                    override suspend fun handle(): OnResult {
                        deleteEvent(eventId!!)
                        return OnResult(
                            if (seasonId != null) "/seasons/$seasonId" else "/",
                            """Slettet øvelse "${event.title}""""
                        )
                    }
                }
                cancelRedirectUri = "/events/$eventId"
                handleAlert = props.handleAlert
                buttonTexts = ButtonTexts(saveButtonText = "Slett øvelse")
            }
        }
    }
}