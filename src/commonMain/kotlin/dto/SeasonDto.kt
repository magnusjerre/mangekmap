package dto

import kotlinx.serialization.Serializable

@Serializable
data class SeasonDto(
    var events: List<SimpleEventDto>,
    var participants: List<SeasonParticipantDto>,
    var name: String,
    var startYear: Int,
    var mangekjemperRequiredEvents: Short,
    var id: Long? = null
)

@Serializable
data class SimpleEventDto(val id: Long, val name: String, val categoryDto: CategoryDto)

@Serializable
data class SeasonParticipantDto(
    val personId: Long,
    val name: String,
    val gender: GenderDto,
    val seasonRank: Int,
    val seasonPoints: Int,
    val isMangekjemper: Boolean,
    val results: List<EventResultDto>,
)

@Serializable
data class EventResultDto(val eventId: Long, val actualRank: Int?, val isAttendanceOnly: Boolean?, val mangekjemperRank: Int?) {
    fun prettyResult(): String {
        val actualRankPretty = if (actualRank != null) {
            "- ($actualRank)${if (isAttendanceOnly == true) "*" else ""}"
        } else ""

        return if (mangekjemperRank == null) {
            actualRankPretty
        } else {
            "$mangekjemperRank $actualRankPretty"
        }
    }
}

@Serializable
class SeasonPostDto(
    var name: String,
    var startYear: Int,
    var mangekjemperRequiredEvents: Short,
)