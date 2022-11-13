package person_events

import components.HeaderTableCell
import components.TableBox
import csstype.Length
import csstype.em
import dto.ParticipantSimpleDto
import events.getParticipations
import kotlinx.coroutines.launch
import kotlinx.js.jso
import mainScope
import mui.material.CircularProgress
import mui.material.FormControlVariant
import mui.material.InputBaseProps
import mui.material.Size
import mui.material.Table
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableContainer
import mui.material.TableHead
import mui.material.TableRow
import mui.material.TextField
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML.h1
import react.key
import react.router.dom.Link
import react.router.useNavigate
import react.router.useParams
import react.useEffectOnce
import react.useState
import kotlin.js.Date

val PersonEvents = FC<Props> { props ->
    val params = useParams()
    val personId = params["personId"]!!.toLong()
    val navigate = useNavigate()

    var personName by useState("")
    var participations by useState(listOf<ParticipantSimpleDto>())
    var filter by useState("")
    var filteredEvents by useState(emptyList<ParticipantSimpleDto>())

    useEffectOnce {
        mainScope.launch {
            val result = getParticipations(personId)
            participations = result.events.sortedBy { Date(it.eventDate).getTime() }
            personName = result.personName
            val regex = Regex(filter, RegexOption.IGNORE_CASE)
            filteredEvents = result.events.sortedBy { Date(it.eventId).getTime() }.filter { filter.trim() == "" || regex.containsMatchIn(it.eventTitle) }
        }
    }

    if (participations.isEmpty()) {
        CircularProgress { }
        return@FC
    }

    h1 {
        +personName
    }

    Link {
        onClick = {
            navigate.invoke(-1)
        }
        +"Tilbake"
    }

    TableBox {
        TextField {
            sx {
                width = 20.em
                marginBottom = 1.em
            }
            label = ReactNode("Filtrer på øvelse (regex, ignore case)")
            variant = FormControlVariant.standard
            asDynamic().InputProps = jso<InputBaseProps> {
                value = filter
                onChange = { changeEvent ->
                    val inputValue = changeEvent.target.asDynamic().value as String
                    console.log("filter: ", inputValue)
                    filter = inputValue
                    val regex = Regex(inputValue, RegexOption.IGNORE_CASE)
                    filteredEvents = participations.filter { inputValue.trim() == "" || regex.containsMatchIn(it.eventTitle)}
                }
            }
        }

        TableContainer {
            sx {
                width = Length.fitContent
            }
            Table {
                size = Size.small
                TableHead {
                    TableRow {
                        HeaderTableCell { +"Øvelse" }
                        HeaderTableCell { +"Plassering" }
                        HeaderTableCell { +"Score" }
                        HeaderTableCell { +"Dato" }
                        HeaderTableCell { +"Kategori" }
                        HeaderTableCell { +"Sesong" }
                        HeaderTableCell { +"Region" }
                    }
                }
                TableBody {
                    for (participation in filteredEvents) {
                        TableRow {
                            key = "${participation.eventId}"
                            TableCell {
                                Link {
                                    to = "/events/${participation.eventId}"
                                    +participation.eventTitle
                                }
                            }
                            TableCell { +"${participation.rank}" }
                            TableCell { +participation.score }
                            TableCell { +participation.eventDate }
                            TableCell { +participation.categoryDto.name }
                            TableCell {
                                Link {
                                    to = "/seasons/${participation.seasonId}"
                                    +participation.seasonName
                                }
                            }
                            TableCell { +participation.region.prettyName() }
                        }
                    }
                }
            }
        }
    }
}