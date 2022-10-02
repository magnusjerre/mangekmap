package persons

import dto.GenderDto
import dto.PersonDto
import csstype.AlignItems
import csstype.Display
import csstype.FlexDirection
import csstype.JustifyContent
import csstype.Padding
import csstype.em
import csstype.px
import kotlinx.coroutines.launch
import kotlinx.js.jso
import mainScope
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.Checkbox
import mui.material.FormControl
import mui.material.FormControlLabel
import mui.material.FormGroup
import mui.material.FormLabel
import mui.material.InputBaseProps
import mui.material.Radio
import mui.material.RadioGroup
import mui.material.TextField
import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.dom.aria.ariaLabelledBy
import react.dom.html.ReactHTML.h1
import react.router.useNavigate
import react.router.useParams
import react.useEffectOnce
import react.useState

val PersonEdit = FC<Props> {
    val params = useParams()
    val personId = params["id"]?.toLongOrNull()
    val navigate = useNavigate()
    var person by useState(PersonDto("", "", GenderDto.MALE, false))

    useEffectOnce {
        if (personId != null) {
            mainScope.launch {
                person = getPerson(personId) ?: person
            }
        }
    }

    Box {
        sx {
            width = 512.px
            padding = Padding(1.em, 1.em)
            display = Display.flex
            flexDirection = FlexDirection.column
            justifyContent = JustifyContent.flexStart
            alignItems = AlignItems.flexStart
        }

        h1 {
            if (personId == null)
                +"Ny bruker"
            else
                +"Rediger bruker"
        }

        TextField {
            id = "name"
            label = ReactNode("Navn")
            fullWidth = true
            asDynamic().InputProps = jso<InputBaseProps> {
                onChange = {
                    person = person.copy(name = it.target.asDynamic().value as String)
                }
                value = person.name
            }
        }

        TextField {
            id = "email"
            label = ReactNode("Epost")
            fullWidth = true
            sx {
                marginTop = 1.em
            }
            asDynamic().InputProps = jso<InputBaseProps> {
                onChange = {
                    person = person.copy(email = it.target.asDynamic().value as String)
                }
                value = person.email
            }
        }

        FormControl {
            sx {
                marginTop = 1.em
            }
            FormLabel {
                id = "gender"
                +"Kjønn"
            }
            RadioGroup {
                ariaLabelledBy = "gender"
                defaultValue = person.gender
                name = "gender-radio-buttons-group"
                value = person.gender
                onChange = { v, _ ->
                    person = person.copy(gender = GenderDto.valueOf(v.target.value))
                }

                FormControlLabel {
                    value = GenderDto.MALE
                    label = ReactNode("Mann")
                    control = Radio.create()
                }

                FormControlLabel {
                    value = GenderDto.FEMALE
                    label = ReactNode("Kvinne")
                    control = Radio.create()
                }
            }
        }

        FormGroup {
            sx {
                marginTop = 1.em
            }
            FormLabel {
                id = "status"
                +"Status"
            }
            FormControlLabel {
                control = Checkbox.create {
                    value = person.retired
                    onChange = { _, _ ->
                        person = person.copy(retired = !person.retired)
                    }
                }
                label = ReactNode("Sluttet?")
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
                        person = if (person.id == null) {
                            postPerson(person)
                        } else {
                            putPerson(person)
                        }
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
                    navigate.invoke("/persons")
                }
                +"Avbryt"
            }
        }
    }
}