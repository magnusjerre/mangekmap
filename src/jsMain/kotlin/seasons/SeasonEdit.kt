package seasons

import MANGEKJEMPER_REQUIRED_EVENTS
import csstype.AlignItems
import csstype.Display
import csstype.FlexDirection
import csstype.JustifyContent
import csstype.Padding
import csstype.em
import csstype.px
import dto.RegionDto
import dto.SeasonPostDto
import kotlinx.coroutines.launch
import kotlinx.js.jso
import mainScope
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.CircularProgress
import mui.material.FormControl
import mui.material.InputBaseProps
import mui.material.InputLabel
import mui.material.MenuItem
import mui.material.Select
import mui.material.TextField
import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML.h1
import react.key
import react.router.useNavigate
import react.router.useParams
import react.useEffectOnce
import react.useState

val SeasonEdit = FC<Props> {
    val seasonId = useParams()["id"]?.toLongOrNull()
    var seasonName by useState("")
    var seasonYear by useState("")
    var seasonRegion by useState(RegionDto.OSLO)
    var mangekjemperRequiredEvents by useState("$MANGEKJEMPER_REQUIRED_EVENTS")
    var fetching by useState(false)
    val navigate = useNavigate()
    var buttonsDisabled by useState(false)

    useEffectOnce {
        mainScope.launch {
            if (seasonId == null) return@launch

            fetching = true
            val season = getSeason(seasonId, excludeEvents = true)
            console.log("season response", season)
            seasonName = season.name
            seasonYear = season.startYear.toString()
            mangekjemperRequiredEvents = season.mangekjemperRequiredEvents.toString()
            seasonRegion = season.region
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

        FormControl {
            sx {
                marginTop = 1.em
            }
            InputLabel {
                id = "region-label"
                +"Region"
            }
            Select {
                labelId = "region-label"
                id = "region"
                value = seasonRegion.name
                label = ReactNode("Region")
                onChange = { ev, _ ->
                    seasonRegion = RegionDto.valueOf(ev.target.value)
                }

                for (region in RegionDto.values()) {
                    MenuItem {
                        key = "${region.ordinal}"
                        value = region.name
                        +(region.name.substring(0, 1) + region.name.lowercase().substring(1))
                    }
                }
            }
        }

        TextField {
            id = "mangekjemper-required-events"
            label = ReactNode("Øvelser for å bli mangekjemper")
            fullWidth = false
            value = mangekjemperRequiredEvents
            sx {
                marginTop = 1.em
            }
            asDynamic().InputProps = jso<InputBaseProps> {
                onChange = {
                    val input = it.target.asDynamic().value as String
                    if (input == "" || input.toShortOrNull() != null) {
                        mangekjemperRequiredEvents = input
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
                disabled = buttonsDisabled
                variant = ButtonVariant.contained
                onClick = {
                    mainScope.launch {
                        buttonsDisabled = true
                        val seasonRequestDto =
                            SeasonPostDto(seasonName, seasonYear.toInt(), mangekjemperRequiredEvents.toShort(), seasonRegion)
                        val seasonResponseDto = if (seasonId == null) postSeason(seasonRequestDto) else putSeason(
                            seasonId,
                            seasonRequestDto
                        )
                        seasonName = seasonResponseDto.name
                        seasonYear = seasonResponseDto.startYear.toString()
                        mangekjemperRequiredEvents = seasonResponseDto.mangekjemperRequiredEvents.toString()
                        seasonRegion = seasonResponseDto.region
                        navigate("/")
                    }
                }
                +"Lagre"
            }
            Button {
                sx {
                    marginLeft = 1.em
                }
                variant = ButtonVariant.outlined
                disabled = buttonsDisabled
                onClick = {
                    navigate("/")
                }
                +"Avbryt"
            }
        }
    }
}