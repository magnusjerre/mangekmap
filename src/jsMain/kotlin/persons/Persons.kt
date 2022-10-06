import csstype.px
import dto.PersonDto
import kotlinx.coroutines.launch
import mui.material.Table
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableContainer
import mui.material.TableHead
import mui.material.TableRow
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

    Link {
        to = "/persons/new"
        +"Ny person"
    }

    TableContainer {
        Table {
            sx {
                maxWidth = 600.px
                width = 600.px
                minWidth = 300.px
            }

            TableHead {
                TableRow {
                    TableCell { +"Navn" }
                    TableCell { +"Epost" }
                    TableCell { +"Kjønn" }
                    TableCell { +"Sluttet?" }
                    TableCell { +"Rediger" }
                }
            }
            TableBody {
                for (person in persons) {
                    TableRow {
                        TableCell { +"${person.name}" }
                        TableCell { +"${person.email}" }
                        TableCell { +"${person.gender}" }
                        TableCell { +"${if (person.retired) "Ja" else "Nei"}" }
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