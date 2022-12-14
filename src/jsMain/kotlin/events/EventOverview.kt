package events

import authentication.getIsAuthenticated
import components.TableBox
import csstype.Display
import csstype.FlexDirection
import csstype.FlexWrap
import csstype.FontWeight
import csstype.Length
import csstype.em
import dto.EventDto
import dto.GenderDto
import kotlinx.coroutines.launch
import mainScope
import mui.material.CircularProgress
import mui.material.Stack
import mui.material.StackDirection
import mui.system.Box
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.span
import react.router.dom.Link
import react.router.useParams
import react.useEffectOnce
import react.useState

val EventOverview = FC<Props> {
    val eventId = useParams()["id"]!!.toLong()
    var event by useState<EventDto>()
    val numberOfMen = event?.participants?.count { it.gender == GenderDto.MALE } ?: 0
    val numberOfWomen = event?.participants?.count { it.gender == GenderDto.FEMALE } ?: 0
    val womenText = if (numberOfWomen == 1) "kvinne" else "kvinner"
    var isAuthenticated by useState(false)

    useEffectOnce {
        mainScope.launch {
            event = getEvent(eventId)
            isAuthenticated = getIsAuthenticated()
        }
    }

    if (event == null || event == undefined) {
        CircularProgress { }
        return@FC
    }

    Box {
        sx {
            display = Display.flex
            flexDirection = FlexDirection.column
            marginTop = 1.em
        }

        h1 { +event!!.title }
        Box {
            sx {
                display = Display.flex
                flexDirection = FlexDirection.column
                fontWeight = FontWeight.bold
                marginBottom = 1.em
            }
            span { +"Dato: ${event!!.date}" }
            span { +"Kategori: ${event!!.category.name}" }
            span { +"Deltakere: $numberOfMen menn og $numberOfWomen $womenText" }
        }

        Stack {
            direction = responsive(StackDirection.row)
            spacing = responsive(2)

            sx {
                width = Length.fitContent
                flexWrap = FlexWrap.wrap
            }
            if (event != null) {
                Link {
                    to = "/seasons/${event!!.seasonId}"
                    +"Tilbake til sesong"
                }
            }

            if (isAuthenticated) {
                Link {
                    to = "/events/$eventId/participants/addremove"
                    +"Legg til/fjern deltakere"
                }
                Link {
                    to = "/events/$eventId/edit"
                    +"Rediger ??velseinfo"
                }
                Link {
                    to = "/events/$eventId/participants/editresults"
                    +"Rediger resultater"
                }
                Link {
                    to = "/events/$eventId/delete"
                    +"Slett ??velse"
                }
            }
        }

        Box {
            sx {
                display = Display.flex
                flexDirection = FlexDirection.row
                flexWrap = FlexWrap.wrap
            }
            TableBox {
                h3 { +"Menn" }
                EventParticipantTable {
                    participants = event!!.participants.filter { it.gender == GenderDto.MALE }
                }
            }

            TableBox {
                h3 { +"Kvinner" }
                EventParticipantTable {
                    participants = event!!.participants.filter { it.gender == GenderDto.FEMALE }
                }
            }
        }
    }
}