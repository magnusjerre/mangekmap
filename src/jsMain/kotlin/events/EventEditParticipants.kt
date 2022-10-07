package events

import csstype.Display
import csstype.FlexDirection
import csstype.em
import dto.EventDto
import dto.GenderDto
import dto.ParticipantDto
import dto.ParticipantPostDto
import kotlinx.coroutines.launch
import kotlinx.js.jso
import kotlinx.js.timers.setTimeout
import mainScope
import mui.material.Alert
import mui.material.AlertColor
import mui.material.AlertVariant
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.CircularProgress
import mui.material.FormControlVariant
import mui.material.InputBaseProps
import mui.material.Size
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableContainer
import mui.material.TableHead
import mui.material.TableRow
import mui.material.TextField
import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.InputType
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h2
import react.key
import react.router.dom.Link
import react.router.useNavigate
import react.router.useParams
import react.useEffectOnce
import react.useState

private fun List<ParticipantDto>.mapReplace(dtoUpdated: ParticipantDto) = map {
    if (it.personId == dtoUpdated.personId) dtoUpdated
    else it
}

val EventEditResults = FC<Props> {
    val eventId = useParams()["id"]!!.toLong()
    var event by useState<EventDto>()
    var men by useState(emptyList<ParticipantDto>())
    var women by useState(emptyList<ParticipantDto>())
    var performingPatch by useState(false)
    var showSuccess by useState(false)
    val navigate = useNavigate()

    useEffectOnce {
        mainScope.launch {
            val eventResponse = getEvent(eventId)
            event = eventResponse
            men = eventResponse.participants.filter { it.gender == GenderDto.MALE }
            women = eventResponse.participants.filter { it.gender == GenderDto.FEMALE }
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
            margin = 1.em
        }

        h1 {
            +"Rediger resultater: ${event!!.title}"
        }

        Link {
            to = "/events/$eventId"
            +"Tilbake"
        }

        h2 { +"Menn" }
        TableContainer {
            TableHead {
                TableRow {
                    TableCell { +"Navn" }
                    TableCell { +"Score" }
                    TableCell { +"Plassering" }
                }
            }

            TableBody {
                for (participant in men) {
                    TableRow {
                        key = "${participant.personId}"
                        TableCell { +participant.name }
                        TableCell {
                            TextField {
                                variant = FormControlVariant.standard
                                asDynamic().InputProps = jso<InputBaseProps> {
                                    value = participant.score
                                    onChange = { changeEvent ->
                                        men =
                                            men.mapReplace(participant.copy(score = changeEvent.target.asDynamic().value as String))
                                    }
                                }
                            }
                        }
                        TableCell {
                            TextField {
                                size = Size.small
                                inputProps = jso {
                                    type = InputType.number
                                }
                                variant = FormControlVariant.standard
                                asDynamic().InputProps = jso<InputBaseProps> {
                                    value = participant.rank
                                    onChange = { changeEvent ->
                                        men =
                                            men.mapReplace(
                                                participant.copy(
                                                    rank = (changeEvent.target.asDynamic().value as String).toInt()
                                                        .coerceIn(0, 1000)
                                                )
                                            )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        h2 { +"Kvinner" }
        TableContainer {
            TableHead {
                TableRow {
                    TableCell { +"Navn" }
                    TableCell { +"Score" }
                    TableCell { +"Plassering" }
                }
            }

            TableBody {
                for (participant in women) {
                    TableRow {
                        key = "${participant.personId}"
                        TableCell { +participant.name }
                        TableCell {
                            TextField {
                                variant = FormControlVariant.standard
                                asDynamic().InputProps = jso<InputBaseProps> {
                                    value = participant.score
                                    onChange = { changeEvent ->
                                        women =
                                            women.mapReplace(participant.copy(score = changeEvent.target.asDynamic().value as String))
                                    }
                                }
                            }
                        }
                        TableCell {
                            TextField {
                                inputProps = jso {
                                    type = InputType.number
                                }
                                variant = FormControlVariant.standard
                                asDynamic().InputProps = jso<InputBaseProps> {
                                    value = participant.rank
                                    onChange = { changeEvent ->
                                        women =
                                            women.mapReplace(
                                                participant.copy(
                                                    rank = (changeEvent.target.asDynamic().value as String).toInt()
                                                        .coerceIn(0, 1000)
                                                )
                                            )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showSuccess) {
            Alert {
                variant = AlertVariant.outlined
                severity = AlertColor.success
                +"Endringer lagret"
            }
        }

        Box {
            sx {
                display = Display.flex
                marginTop = 1.em
            }

            Button {
                disabled = performingPatch
                variant = ButtonVariant.contained
                onClick = {
                    mainScope.launch {
                        performingPatch = true
                        patchParticipants(eventId, (men + women).map {
                            ParticipantPostDto(
                                personId = it.personId,
                                rank = it.rank,
                                score = it.score
                            )
                        })
                        performingPatch = false
                        showSuccess = true

                        setTimeout({
                            showSuccess = false
                        }, 1500)
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
                    navigate("/events/$eventId")
                }
                +"Avbryt"
            }
        }
    }
}