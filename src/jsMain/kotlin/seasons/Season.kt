package seasons

import csstype.em
import dto.GenderDto
import dto.SeasonDto
import dto.SeasonParticipantDto
import kotlinx.coroutines.launch
import mainScope
import mui.material.List as MuiList
import mui.material.ListItem
import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h2
import react.key
import react.router.dom.Link
import react.router.useParams
import react.useEffectOnce
import react.useState

val Season = FC<Props> {
    val seasonId = useParams()["id"]!!.toLong()
    var season by useState(SeasonDto(emptyList(), emptyList() , "Unknown", 0))
    var male by useState<List<SeasonParticipantDto>>(emptyList())
    var female by useState<List<SeasonParticipantDto>>(emptyList())

    useEffectOnce {
        mainScope.launch {
            try {
                val season1 = getSeason(seasonId, true)
                season = season1
                male = season1.participants.filter { it.gender == GenderDto.MALE }
                female = season1.participants.filter { it.gender == GenderDto.FEMALE }
            } catch (e: Exception) {
                console.log(e)
            }
        }
    }

    Box {
        sx {
            margin = 1.em
        }

        h1 {
            +season.name
        }

        h2 {
            +"${season.startYear}"
        }

        MuiList {
            for (event in season.events) {
                ListItem {
                    key = "${event.id}"
                    Link {
                        to = "/events/${event.id}"
                        +event.name
                    }
                }
            }
        }

        SeasonResultTable {
            events = season.events
            participants = female
        }

        Box {
            sx {
                marginTop = 2.em
            }
        }

        SeasonResultTable {
            events = season.events
            participants = male
        }
    }
}