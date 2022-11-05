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
    val results: List<EventResult>,
)

@Serializable
data class EventResult(val eventId: Long, val actualRank: Int?, val mangekjemperRank: Int?) {
    fun prettyResult(): String = if (mangekjemperRank == null) {
        if (actualRank == null)
            ""
        else
            "- ($actualRank)"
    } else {
        "$mangekjemperRank (${actualRank?.toString() ?: ""})"
    }
}

@Serializable
class SeasonPostDto(
    var name: String,
    var startYear: Int,
    var mangekjemperRequiredEvents: Short,
)