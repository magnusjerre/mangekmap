package seasons

import csstype.em
import dto.SeasonDto
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
    var season by useState(SeasonDto(emptyList(), "Unknown", 0))

    useEffectOnce {
        mainScope.launch {
            try {
                season = getSeason(seasonId, true)
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
                        +event.title
                    }
                }
            }
        }
    }
}