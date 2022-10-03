package seasons

import csstype.em
import dto.SeasonDto
import kotlinx.coroutines.launch
import mainScope
import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h2
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
    }
}