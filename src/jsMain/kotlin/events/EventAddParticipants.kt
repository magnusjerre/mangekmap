package events

import csstype.Display
import csstype.FlexDirection
import csstype.em
import dto.ParticipantPostDto
import kotlinx.coroutines.launch
import kotlinx.js.timers.setTimeout
import mainScope
import mui.material.Alert
import mui.material.AlertColor
import mui.material.AlertVariant
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.Checkbox
import mui.material.CircularProgress
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableContainer
import mui.material.TableHead
import mui.material.TableRow
import mui.system.Box
import mui.system.sx
import persons.fetchPersons
import react.FC
import react.Props
import react.dom.html.ReactHTML.h1
import react.key
import react.router.useNavigate
import react.router.useParams
import react.useEffectOnce
import react.useState

//external interface EventAddParticipantsProps : Props {
//    var participatinhPersonsIds: List<Long>?
//    var eventName: String?
//}

private class PersonSimplified(val id: Long, val name: String, val included: Boolean)

val EventAddParticipants = FC<Props> { props ->
    var allPersons by useState<List<PersonSimplified>>(emptyList())
    val eventId = useParams()["id"]!!.toLong()
    val navigate = useNavigate()
    var eventName by useState("Ukjent")
    var performingPatch by useState(false)
    var showSuccess by useState(false)

    useEffectOnce {
        mainScope.launch {
            val event = getEvent(eventId)
            eventName = event.title
            val fetchedPersons = fetchPersons(includeRetired = false)
            allPersons = fetchPersons(includeRetired = false)
                .map { PersonSimplified(it.id!!, it.name, event.participants.any { p -> p.personId == it.id }) }
                .sortedBy { it.name }
        }
    }
    console.log("allPersons", allPersons)

    Box {
        sx {
            display = Display.flex
            flexDirection = FlexDirection.column
            margin = 1.em
        }

        h1 { +"Legg til/fjern deltakere for: $eventName" }

        TableContainer {
            TableHead {
                TableRow {
                    TableCell { +"Inkluder" }
                    TableCell { +"Navn" }
                }
            }
            TableBody {
                for (person in allPersons) {
                    TableRow {
                        key = "${person.id}"
                        TableCell {
                            Checkbox {
                                checked = person.included
                                value = person.included
                                onChange = { _, _ ->
                                    allPersons = allPersons.map {
                                        if (it == person) PersonSimplified(
                                            it.id,
                                            it.name,
                                            !it.included
                                        ) else it
                                    }
                                }
                            }
                        }
                        TableCell { +person.name }
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
                        patchParticipants(eventId, allPersons.filter { it.included }.map { ParticipantPostDto(it.id) })
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
                variant = ButtonVariant.outlined
                onClick = {
                    navigate("/events/$eventId")
                }
                +"Avbryt"
            }
        }
    }
}