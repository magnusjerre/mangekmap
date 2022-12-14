package events

import components.HeaderTableCell
import components.PersonEventsNameLink
import csstype.px
import dto.ParticipantDto
import mui.material.Size
import mui.material.Table
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableContainer
import mui.material.TableHead
import mui.material.TableRow
import mui.system.sx
import react.FC
import react.Props
import react.key

external interface EventParticipantTableProps : Props {
    var participants: List<ParticipantDto>
}

val EventParticipantTable = FC<EventParticipantTableProps> { props ->
    val participants = props.participants.sorted()
    val isTeamBased = participants.firstOrNull()?.teamNumber != null

    TableContainer {
        Table {
            sx {
                maxWidth = 400.px
                width = 400.px
            }
            size = Size.small

            TableHead {
                TableRow {
                    HeaderTableCell { +"Navn" }
                    if (isTeamBased) {
                        HeaderTableCell { +"Lagnummer" }
                    }
                    HeaderTableCell { +"Score" }
                    HeaderTableCell { +"Plassering" }
                    HeaderTableCell { +"Kun oppmøte?" }
                }
            }

            TableBody {
                for (participant in participants) {
                    TableRow {
                        key = "${participant.personId}"
                        TableCell {
                            PersonEventsNameLink {
                                id = participant.personId
                                name = participant.name
                            }
                        }
                        if (isTeamBased) {
                            TableCell { +"${participant.teamNumber}" }
                        }
                        TableCell { +participant.score }
                        TableCell { +"${participant.rank}" }
                        TableCell {
                            if (participant.isAttendanceOnly) {
                                +"✔"
                            }
                        }
                    }
                }
            }
        }
    }
}