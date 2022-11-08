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
    val seasonPenaltyPoints: SeasonPenaltyPointsDto?,
    val isMangekjemper: Boolean,
    val results: List<EventResultDto>,
)

@Serializable
data class SeasonPenaltyPointsDto(
    val pointsPerMissingEvent: Int,
    val numberOfMissingEvents: Int
) {
    val penaltyPoints: Int = pointsPerMissingEvent * numberOfMissingEvents
}


@Serializable
data class EventResultDto(
    val eventId: Long,
    val seasonId: Long,
    val actualRank: Int?,
    val eventPoints: Int,
    val eventCategoryName: String,
    val eventPointsReason: EventPointsReasonDto?,
    val isAttendanceOnly: Boolean?,
    val mangekjemperRank: Int?) {
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
    var region: RegionDto
)

@Serializable
enum class RegionDto {
    OSLO, TRONDHEIM, BERGEN
}

@Serializable
enum class EventPointsReasonDto {
    NOT_INCLUDED, NOT_MANGEKJEMPER, MANGEKJEMPER, MANGEKJEMPER_TOO_MANY_OF_SAME, OTHER_REGION_NOT_MANGEKJEMPER, OTHER_REGION_MANGEKJEMPER, OTHER_REGION_NOT_INCLUDED
}