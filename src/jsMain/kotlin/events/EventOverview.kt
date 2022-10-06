package events

import authentication.getIsAuthenticated
import csstype.Display
import csstype.FlexDirection
import csstype.FontWeight
import csstype.em
import dto.EventDto
import dto.GenderDto
import kotlinx.coroutines.launch
import mainScope
import mui.material.CircularProgress
import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.p
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
            }
            span { +"Dato: ${event!!.date}" }
            span { +"Kategori: ${event!!.category.name}" }
            span { +"Deltakere: $numberOfMen menn og $numberOfWomen $womenText" }
        }

        if (isAuthenticated) {
            Link {
                to = "/events/$eventId/participants/addremove"
                +"Legg til/fjern deltakere"
            }
        } else {
            p {
                +"Ikke logget inn"
            }
        }

        h2 { +"Resultater" }

        h3 { +"Menn"}
        EventParticipantTable {
            participants = event!!.participants.filter { it.gender == GenderDto.MALE }
        }

        h3 { +"Kvinner"}
        EventParticipantTable {
            participants = event!!.participants.filter { it.gender == GenderDto.FEMALE }
        }
    }
}