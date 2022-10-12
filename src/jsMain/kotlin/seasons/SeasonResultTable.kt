package seasons

import csstype.Color
import csstype.Display
import csstype.FontStyle
import csstype.FontWeight
import csstype.None
import csstype.TextAlign
import csstype.TextDecoration
import csstype.em
import csstype.pc
import csstype.px
import dto.GenderDto
import dto.SeasonParticipantDto
import dto.SimpleEventDto
import emotion.react.css
import emotion.styled.styled
import kotlinx.js.jso
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableCellProps
import mui.material.TableContainer
import mui.material.TableHead
import mui.material.TableRow
import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.span
import react.key
import react.router.dom.Link

external interface SeasonResultTableProps : Props {
    var events: List<SimpleEventDto>
    var participants: List<SeasonParticipantDto>
}

external interface RankElementProps : Props {
    var rank: Int
}

val RankElement = FC<RankElementProps> { props ->
    span {
        css {
            width = 2.em
            height = 2.em
            display = Display.inlineBlock
            fontWeight = FontWeight.bold
            lineHeight = 2.em
            if (props.rank < 4) {
                backgroundColor =
                    if (props.rank == 1) Color("#C98910") else if (props.rank == 2) Color("#A8A8A8") else Color("#965A38")
                borderRadius = 100.pc
                color = Color("white")
            }
        }

        +"${props.rank}"
    }

}

private val CenteredTableCell = styled(TableCell)(){tableCellProps: TableCellProps ->
    jso{
        textAlign = TextAlign.center
    }
}

val SeasonResultTable = FC<SeasonResultTableProps> { props ->
    val header = props.participants.firstOrNull()?.gender?.let { if (it == GenderDto.MALE) "Menn" else "Kvinner" } ?: ""

    Box {
        h2 { +header }
        TableContainer {
            TableHead {
                TableRow {
                    TableCell { +"Plassering" }
                    TableCell {
                        sx {
                            width = 150.px
                        }
                        +"Navn"
                    }
                    TableCell { +"#Øvelser" }
                    for (event in props.events) {
                        TableCell {
                            sx {
                                backgroundColor = Color(event.categoryDto.color)
                                color = Color("white")
                                minWidth = 4.em
                            }
                            key = "${event.id}"
                            Link {
                                to = "/events/${event.id}"
                                css {
                                    textDecoration = None.none
                                    color = Color("white")
                                }
                                +event.name
                            }
                        }
                    }
                    TableCell { +"Poeng" }
                    TableCell { +"Mangekjemper?" }
                }
            }

            TableBody {
                for (participant in props.participants) {
                    TableRow {
                        key = "${participant.personId}"
                        CenteredTableCell {
                            RankElement {
                                rank = participant.seasonRank
                            }
                        }
                        TableCell { +participant.name }
                        CenteredTableCell { +"${participant.results.size}" }
                        for (event in props.events) {
                            CenteredTableCell {
                                val result = participant.results.find { it.eventId == event.id }?.prettyResult() ?: ""
                                +result
                            }
                        }
                        CenteredTableCell { +"${participant.seasonPoints}" }
                        CenteredTableCell {
                            if (participant.isMangekjemper)
                                +"✔"
                             else
                                +"\uD83D\uDEAB"

                        }
                    }
                }
            }
        }
    }
}