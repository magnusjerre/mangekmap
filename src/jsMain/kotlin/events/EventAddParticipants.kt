package events

import AppAlertUsageProps
import components.ButtonTexts
import components.ModificationButtons
import components.OnHandler
import components.OnResult
import csstype.Display
import csstype.FlexDirection
import csstype.em
import dto.ParticipantPostDto
import kotlinx.coroutines.launch
import mainScope
import mui.material.Checkbox
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableContainer
import mui.material.TableHead
import mui.material.TableRow
import mui.system.Box
import mui.system.sx
import persons.fetchPersons
import react.FC
import react.dom.html.ReactHTML.h1
import react.key
import react.router.dom.Link
import react.router.useParams
import react.useEffectOnce
import react.useState

private class PersonSimplified(val id: Long, val name: String, val included: Boolean)

val EventAddParticipants = FC<AppAlertUsageProps> { props ->
    var allPersons by useState<List<PersonSimplified>>(emptyList())
    val eventId = useParams()["id"]!!.toLong()
    val redirectUri = "/events/$eventId"
    var eventName by useState("Ukjent")

    useEffectOnce {
        mainScope.launch {
            val event = getEvent(eventId)
            eventName = event.title
            allPersons = fetchPersons(includeRetired = false)
                .map { PersonSimplified(it.id!!, it.name, event.participants.any { p -> p.personId == it.id }) }
                .sortedBy { it.name }
        }
    }

    Box {
        sx {
            display = Display.flex
            flexDirection = FlexDirection.column
            margin = 1.em
        }

        h1 { +"Legg til/fjern deltakere for: $eventName" }

        Link {
            to = "/events/$eventId"
            +"Tilbake"
        }

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

        ModificationButtons {
            onSave = object : OnHandler {
                override suspend fun handle(): OnResult {
                    patchParticipants(eventId, allPersons.filter { it.included }.map { it.id })
                    return OnResult(redirectUri, "Deltakerendringer lagret")
                }
            }
            cancelRedirectUri = redirectUri
            buttonTexts = ButtonTexts()
            handleAlert = props.handleAlert
        }
    }
}