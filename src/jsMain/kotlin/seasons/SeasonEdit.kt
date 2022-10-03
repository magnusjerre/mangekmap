package seasons

import csstype.AlignItems
import csstype.Display
import csstype.FlexDirection
import csstype.JustifyContent
import csstype.Padding
import csstype.em
import csstype.px
import dto.SeasonPostDto
import kotlinx.coroutines.launch
import kotlinx.js.jso
import mainScope
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.CircularProgress
import mui.material.InputBaseProps
import mui.material.TextField
import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML.h1
import react.router.useNavigate
import react.router.useParams
import react.useEffectOnce
import react.useState

val SeasonEdit = FC<Props> {
    val seasonId = useParams()["id"]?.toLongOrNull()
    var seasonName by useState("")
    var seasonYear by useState("")
    var fetching by useState(false)
    val navigate = useNavigate()

    useEffectOnce {
        mainScope.launch {
            if (seasonId == null) return@launch

            fetching = true
            val season = getSeason(seasonId, excludeEvents = true)
            console.log("season response", season)
            seasonName = season.name
            seasonYear = season.startYear.toString()
            fetching = false
        }
    }

    if (fetching) {
        CircularProgress {}
        return@FC
    }

    Box {
        sx {
            width = 256.px
            padding = Padding(1.em, 1.em)
            display = Display.flex
            flexDirection = FlexDirection.column
            justifyContent = JustifyContent.flexStart
            alignItems = AlignItems.flexStart
        }

        h1 {
            if (seasonId == null)
                +"Ny sesong"
            else
                +"Rediger sesong"
        }

        TextField {
            id = "name"
            label = ReactNode("Navn")
            fullWidth = true
            value = seasonName
            asDynamic().InputProps = jso<InputBaseProps> {
                onChange = {
                    seasonName = it.target.asDynamic().value as String
                }
            }
        }

        TextField {
            id = "start-year"
            label = ReactNode("Start år")
            fullWidth = true
            value = seasonYear
            sx {
                marginTop = 1.em
            }
            asDynamic().InputProps = jso<InputBaseProps> {
                onChange = {
                    val input = it.target.asDynamic().value as String
                    if ("""\d*""".toRegex().matches(input)) {
                        seasonYear = input
                    } else {
                        console.log("Må være tall")
                    }
                }
            }
        }

        Box {
            sx {
                display = Display.flex
                marginTop = 1.em
            }

            Button {
                variant = ButtonVariant.contained
                onClick = {
                    mainScope.launch {
                        console.log("trykket på lagre")
                        val seasonRequestDto = SeasonPostDto(seasonName, seasonYear.toInt())
                        val seasonResponseDto = if (seasonId == null) postSeason(seasonRequestDto) else putSeason(seasonId, seasonRequestDto)
                        seasonName = seasonResponseDto.name
                        seasonYear = seasonResponseDto.startYear.toString()
                    }
                }
                +"Lagre"
            }

            Button {
                sx {
                    marginLeft = 1.em
                }
                variant = ButtonVariant.outlined
                onClick = {
                    navigate.invoke("/seasons")
                }
                +"Avbryt"
            }
        }


    }

}