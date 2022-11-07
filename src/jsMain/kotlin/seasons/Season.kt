package seasons

import authentication.getIsAuthenticated
import categories.getCategories
import csstype.Color
import csstype.FontWeight
import csstype.em
import dto.CategoryDto
import dto.GenderDto
import dto.SeasonDto
import dto.SeasonParticipantDto
import emotion.react.css
import kotlinx.coroutines.launch
import mainScope
import mui.system.Box
import mui.system.Stack
import mui.system.StackDirection
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.span
import react.router.dom.Link
import react.router.useParams
import react.useEffectOnce
import react.useState

val Season = FC<Props> {
    val seasonId = useParams()["id"]!!.toLong()
    var season by useState(SeasonDto(emptyList(), emptyList() , "Unknown", 0, 8))
    var categories by useState(emptyList<CategoryDto>())
    var male by useState<List<SeasonParticipantDto>>(emptyList())
    var female by useState<List<SeasonParticipantDto>>(emptyList())
    var isAuthenticated by useState(false)

    useEffectOnce {
        mainScope.launch {
            try {
                isAuthenticated = getIsAuthenticated()
                categories = getCategories()

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

        Box {
            sx {
                marginTop = 2.em
                marginBottom = 2.em
            }
            for (category in categories) {
                span {
                    css {
                        color = Color("white")
                        fontWeight = FontWeight.bold
                        fontSize = 0.75.em
                        backgroundColor = Color(category.color)
                        padding = 1.em
                        marginRight = 0.25.em
                    }
                    +category.name
                }
            }
        }

        Stack {
            direction = responsive(StackDirection.row)
            spacing = responsive(2)
            Link {
                to = "/"
                +"Tilbake til forside"
            }

            if (isAuthenticated) {
                Link {
                    to = "/seasons/$seasonId/events"
                    +"Legg til ny øvelse"
                }
            }
        }

        SeasonResultTable {
            events = season.events
            participants = female
            this.seasonId = seasonId
        }

        Box {
            sx {
                marginTop = 2.em
            }
        }

        SeasonResultTable {
            events = season.events
            participants = male
            this.seasonId = seasonId
        }
    }
}