import components.HeaderTableCell
import csstype.Length
import csstype.em
import csstype.px
import dto.GenderDto
import dto.PersonDto
import kotlinx.coroutines.launch
import mui.material.Paper
import mui.material.Size
import mui.material.Table
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableContainer
import mui.material.TableHead
import mui.material.TableRow
import mui.system.Stack
import mui.system.sx
import persons.fetchPersons
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.router.dom.Link
import react.useEffectOnce
import react.useState


val Persons = FC<Props> {
    ReactHTML.h1 {
        +"Personer"
    }

    var persons by useState<List<PersonDto>>(listOf())

    useEffectOnce {
        mainScope.launch {
            val personsResponse = fetchPersons(includeRetired = true)
            persons = personsResponse
        }
    }

    Stack {
        Link {
            to = "/"
            +"Tilbake til forside"
        }
        Link {
            to = "/persons/new"
            +"Ny person"
        }
    }

    TableContainer {
        component = Paper
        sx {
            padding = tableBoxPadding
            width = Length.fitContent
        }
        Table {
            size = Size.small
            TableHead {
                TableRow {
                    HeaderTableCell { +"Navn" }
                    HeaderTableCell { +"Epost" }
                    HeaderTableCell { +"Kjønn" }
                    HeaderTableCell { +"Sluttet?" }
                    HeaderTableCell { +"Rediger" }
                }
            }
            TableBody {
                for (person in persons) {
                    TableRow {
                        TableCell { +person.name }
                        TableCell { +person.email }
                        TableCell { +if (person.gender == GenderDto.MALE) "Mann" else "Kvinne" }
                        TableCell { +if (person.retired) "Ja" else "Nei" }
                        TableCell {
                            Link {
                                to = "/persons/${person.id}"
                                +"Rediger"
                            }
                        }
                    }
                }
            }
        }
    }
}