package seasons

import components.HeaderTableCell
import csstype.BoxShadow
import csstype.BoxShadowInset
import csstype.Color
import csstype.Display
import csstype.FontWeight
import csstype.Length
import csstype.NamedColor
import csstype.None
import csstype.Overflow
import csstype.Padding
import csstype.TextAlign
import csstype.em
import csstype.pc
import csstype.px
import csstype.rgba
import dto.GenderDto
import dto.SeasonParticipantDto
import dto.SimpleEventDto
import emotion.react.css
import emotion.styled.styled
import kotlinx.js.jso
import mui.material.Size
import mui.material.Table
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableCellPadding
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
            sx {
                overflowX = Overflow.visible
                width = Length.fitContent
            }
            Table {
                stickyHeader = true
                size = Size.small
                TableHead {
                    TableRow {
                        HeaderTableCell { +"Plassering" }
                        HeaderTableCell {
                            sx {
                                width = 12.em
                            }
                            +"Navn"
                        }
                        HeaderTableCell { +"#Øvelser" }
                        HeaderTableCell { +"Poeng" }
                        HeaderTableCell { +"Mangekjemper?" }
                        for (event in props.events) {
                            HeaderTableCell {
                                backgroundColor = Color(event.categoryDto.color)
                                highlightOnHover = true
                                key = "${event.id}"
                                Link {
                                    to = "/events/${event.id}"
                                    css {
                                        textDecoration = None.none
                                        color = NamedColor.white
                                    }
                                    +event.name
                                }
                            }
                        }
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
                            CenteredTableCell { +"${participant.seasonPoints}" }
                            CenteredTableCell {
                                if (participant.isMangekjemper)
                                    +"✔"
                                else
                                    +"\uD83D\uDEAB"
                            }
                            for (event in props.events) {
                                CenteredTableCell {
                                    val result =
                                        participant.results.find { it.eventId == event.id }?.prettyResult() ?: ""
                                    +result
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}