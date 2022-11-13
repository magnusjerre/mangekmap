package dto

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
class EventDto(
    val date: String,
    val title: String,
    val category: CategoryDto,
    val venue: String,
    val isTeamBased: Boolean,
    val participants: Collection<ParticipantDto>,
    val id: Long,
    val seasonId: Long,
)

@Serializable
data class ParticipantDto(
    @Required
    val rank: Int,
    val isAttendanceOnly: Boolean,
    val score: String,
    val teamNumber: Int?,
    val name: String,
    val gender: GenderDto,
    val personId: Long,
) : Comparable<ParticipantDto> {
    override fun compareTo(other: ParticipantDto): Int = rank.compareTo(other.rank)
}


@Serializable
data class PersonEventsDto(
    val personId: Long,
    val personName: String,
    val events: List<ParticipantSimpleDto>
)

@Serializable
data class ParticipantSimpleDto(
    val eventId: Long,
    val eventTitle: String,
    val eventDate: String,
    val categoryDto: CategoryDto,
    val rank: Int,
    val isAttendanceOnly: Boolean,
    val score: String,
    val seasonId: Long,
    val seasonName: String,
    val region: RegionDto)

@Serializable
data class EventPostDto(
    val date: String,
    val title: String,
    val categoryId: Long,
    val venue: String,
    val isTeamBased: Boolean
)

@Serializable
class ParticipantPostDto(
    val personId: Long,
    val rank: Int? = null,
    val isAttendanceOnly: Boolean? = null,
    val score: String? = null,
    val teamNumber: Int? = null,
)