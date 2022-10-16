package events

import csstype.px
import dto.ParticipantDto
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
    val participants = props.participants//.sorted()
    val isTeamBased = participants.firstOrNull()?.teamNumber != null

    TableContainer {
        Table {
            sx {
                maxWidth = 400.px
                width = 400.px
            }

            TableHead {
                TableRow {
                    TableCell { +"Navn" }
                    if (isTeamBased) {
                        TableCell { +"Lagnummer" }
                    }
                    TableCell { +"Score" }
                    TableCell { +"Rank" }
                }
            }

            TableBody {
                for (participant in participants) {
                    TableRow {
                        key = "${participant.personId}"
                        TableCell { +participant.name }
                        if (isTeamBased) {
                            TableCell { +"${participant.teamNumber}" }
                        }
                        TableCell { +participant.score }
                        TableCell { +"${participant.rank}" }
                    }
                }
            }
        }
    }
}