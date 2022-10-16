package events

import categories.getCategories
import csstype.Display
import csstype.em
import csstype.px
import dto.CategoryDto
import dto.EventPostDto
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
import mui.material.FormControl
import mui.material.FormControlLabel
import mui.material.FormGroup
import mui.material.FormLabel
import mui.material.InputBaseProps
import mui.material.InputLabel
import mui.material.MenuItem
import mui.material.Select
import mui.material.TextField
import mui.system.Box
import mui.system.Stack
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.dom.html.InputType
import react.dom.html.ReactHTML.h1
import react.dom.onChange
import react.key
import react.router.dom.Link
import react.router.useNavigate
import react.router.useParams
import react.useEffectOnce
import react.useState
import seasons.postSeasonEvent


val EventEdit = FC<Props> {
    val params = useParams()
    var seasonId by useState(params["seasonId"]?.toLong())
    var eventId by useState(params["eventId"]?.toLong())
    var categories by useState<List<CategoryDto>>(emptyList())
    var event by useState(EventPostDto(date = "", title = "", categoryId = 0, venue = "", isTeamBased = false))
    var showSuccess by useState(false)
    var loading by useState(false)
    val navigate = useNavigate()

    if (eventId == null && seasonId == null) {
        Alert {
            variant = AlertVariant.filled
            color = AlertColor.error
            +"Må oppgi seasonId eller eventId"
        }
        return@FC
    }

    useEffectOnce {
        mainScope.launch {
            val categoriesDto = getCategories()
            categories = categoriesDto

            val eventDto = eventId?.let { getEvent(it) }
            event = eventDto?.let {
                EventPostDto(
                    date = it.date,
                    title = it.title,
                    categoryId = it.category.id,
                    venue = it.venue,
                    isTeamBased = it.isTeamBased
                )
            } ?: EventPostDto(date = "", title = "", categoryId = categoriesDto.first().id, venue = "", isTeamBased = false)
            if (eventDto != null) {
                seasonId = eventDto.seasonId
            }
        }
    }

    Box {
        sx {
            width = 400.px
        }

        h1 {
            if (eventId == null)
                +"Ny øvelse"
            else
                +"Rediger øvelse"
        }

        Stack {
            asDynamic().spacing = 3

            Link {
                to = if (eventId != null) {
                    "/events/$eventId"
                } else {
                    "/seasons/$seasonId"
                }
                +"Tilbake til øvelse"
            }

            TextField {
                id = "title"
                label = ReactNode("Navn")
                fullWidth = true
                asDynamic().InputProps = jso<InputBaseProps> {
                    onChange = {
                        event = event.copy(title = it.target.asDynamic().value as String)
                    }
                    value = event.title
                }
            }

            TextField {
                id = "venue"
                label = ReactNode("Sted")
                fullWidth = true
                asDynamic().InputProps = jso<InputBaseProps> {
                    onChange = {
                        event = event.copy(venue = it.target.asDynamic().value as String)
                    }
                    value = event.venue
                }
            }

            TextField {
                id = "date"
                label = ReactNode("Dato")
                type = InputType.date
                asDynamic().InputProps = jso {
                    onChange = {
                        event = event.copy(date = it.target.asDynamic().value as String)
                    }
                    value = event.date
                }
            }

            FormGroup {
                sx {
                    marginTop = 1.em
                }
                FormLabel {
                    id = "isTeamBased"
                    +"Lagøvelse"
                }
                FormControlLabel {
                    control = Checkbox.create {
                        checked = event.isTeamBased
                        value = event.isTeamBased
                        onChange = { _, _ ->
                            event = event.copy(isTeamBased = !event.isTeamBased)
                        }
                    }
                    label = ReactNode("Lagøvelse?")
                }
            }

            FormControl {
                InputLabel {
                    id = "category-label"
                    +"Kategori"
                }
                Select {
                    labelId = "category-label"
                    id = "category"
                    value = "${event.categoryId}"
                    label = ReactNode("Kategori")
                    onChange = { ev, _ ->
                        event = event.copy(categoryId = ev.target.value.toLong())
                    }

                    for (category in categories) {
                        MenuItem {
                            key = "${category.id}"
                            value = "${category.id}"
                            +category.name
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
                disabled = loading
                variant = ButtonVariant.contained
                onClick = {
                    mainScope.launch {
                        loading = true
                        if (seasonId != null && eventId == null) {
                            val eventDto = postSeasonEvent(seasonId!!, event)
                            eventId = eventDto.id
                            event = eventDto.let { EventPostDto(date = it.date, title = it.title, categoryId = it.category.id, venue = it.venue, isTeamBased = it.isTeamBased) }
                        } else {
                            patchEvent(eventId!!, event)
                        }
                        loading = false
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
                    console.log("seasonId", seasonId.toString().toLong())
                    console.log("events", eventId)
                    console.log("params", params)
                    if (eventId != null) {
                        navigate("/events/$eventId")
                    } else {
                        navigate("/seasons/$seasonId")
                    }
                }
                +"Avbryt"
            }
        }
    }
}