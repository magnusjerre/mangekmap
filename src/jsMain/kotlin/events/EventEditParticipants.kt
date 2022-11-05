package events

import components.HeaderTableCell
import components.TableBox
import csstype.Display
import csstype.FlexDirection
import csstype.FlexWrap
import csstype.JustifyContent
import csstype.Length
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
import mui.material.Checkbox
import mui.material.CircularProgress
import mui.material.FormControlVariant
import mui.material.InputBaseProps
import mui.material.Paper
import mui.material.Size
import mui.material.Table
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
import tableBoxPadding

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
            men = eventResponse.participants.filter { it.gender == GenderDto.MALE }.sorted()
            women = eventResponse.participants.filter { it.gender == GenderDto.FEMALE }.sorted()
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

        Box {
            sx {
                display = Display.flex
                flexDirection = FlexDirection.row
                flexWrap = FlexWrap.wrap
            }
            TableBox {
                h2 { +"Menn" }
                TableContainer {
                    Table {
                        size = Size.small
                        TableHead {
                            TableRow {
                                HeaderTableCell {
                                    sx {
                                        width = 12.em
                                    }
                                    +"Navn"
                                }
                                if (event?.isTeamBased == true) {
                                    HeaderTableCell { +"Lagnummer" }
                                }
                                HeaderTableCell { +"Score" }
                                HeaderTableCell { +"Plassering" }
                                HeaderTableCell { +"Kun oppmøte?" }
                            }
                        }

                        TableBody {
                            for (participant in men) {
                                TableRow {
                                    key = "${participant.personId}"
                                    TableCell { +participant.name }
                                    if (event?.isTeamBased == true) {
                                        TableCell {
                                            TextField {
                                                inputProps = jso {
                                                    type = InputType.number
                                                }
                                                variant = FormControlVariant.standard
                                                asDynamic().InputProps = jso<InputBaseProps> {
                                                    value = participant.teamNumber
                                                    onChange = { changeEvent ->
                                                        men =
                                                            men.mapReplace(
                                                                participant.copy(
                                                                    teamNumber = (changeEvent.target.asDynamic().value as String).toInt()
                                                                        .coerceIn(0, 1000)
                                                                )
                                                            )
                                                    }
                                                }
                                            }
                                        }
                                    }
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
                                    TableCell {
                                        Checkbox {
                                            checked = participant.isAttendanceOnly
                                            value = participant.isAttendanceOnly
                                            onChange = { _, _ ->
                                                val newAttendanceValue = !participant.isAttendanceOnly
                                                men = men.mapReplace(
                                                    participant.copy(
                                                        isAttendanceOnly = newAttendanceValue,
                                                        rank = if (newAttendanceValue) men.count() else participant.rank)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            TableBox {
                h2 { +"Kvinner" }
                TableContainer {
                    Table {
                        size = Size.small
                        TableHead {
                            TableRow {
                                HeaderTableCell {
                                    sx {
                                        width = 12.em
                                    }
                                    +"Navn"
                                }
                                HeaderTableCell { +"Score" }
                                if (event?.isTeamBased == true) {
                                    HeaderTableCell { +"Lagnummer" }
                                }
                                HeaderTableCell { +"Plassering" }
                                HeaderTableCell { +"Kun oppmøte?" }
                            }
                        }

                        TableBody {
                            for (participant in women) {
                                TableRow {
                                    key = "${participant.personId}"
                                    TableCell { +participant.name }
                                    if (event?.isTeamBased == true) {
                                        TableCell {
                                            TextField {
                                                inputProps = jso {
                                                    type = InputType.number
                                                }
                                                variant = FormControlVariant.standard
                                                asDynamic().InputProps = jso<InputBaseProps> {
                                                    value = participant.teamNumber
                                                    onChange = { changeEvent ->
                                                        women =
                                                            women.mapReplace(
                                                                participant.copy(
                                                                    teamNumber = (changeEvent.target.asDynamic().value as String).toInt()
                                                                        .coerceIn(0, 1000)
                                                                )
                                                            )
                                                    }
                                                }
                                            }
                                        }
                                    }
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
                                    TableCell {
                                        Checkbox {
                                            checked = participant.isAttendanceOnly
                                            value = participant.isAttendanceOnly
                                            onChange = { _, _ ->
                                                val newAttendanceValue = !participant.isAttendanceOnly
                                                women = women.mapReplace(
                                                    participant.copy(
                                                        isAttendanceOnly = newAttendanceValue,
                                                        rank = if (newAttendanceValue) women.count() else participant.rank)
                                                    )
                                            }
                                        }
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
                                isAttendanceOnly = it.isAttendanceOnly,
                                score = it.score,
                                teamNumber = it.teamNumber
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