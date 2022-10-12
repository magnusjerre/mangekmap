package dto

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
class EventDto(
    val date: String,
    val title: String,
    val category: CategoryDto,
    val venue: String,
    val participants: Collection<ParticipantDto>,
    val id: Long,
    val seasonId: Long,
)

@Serializable
data class ParticipantDto(
    @Required
    val rank: Int,
    val score: String,
    val name: String,
    val gender: GenderDto,
    val personId: Long,
) : Comparable<ParticipantDto> {
    override fun compareTo(other: ParticipantDto): Int = rank.compareTo(other.rank)
}

@Serializable
data class EventPostDto(
    val date: String,
    val title: String,
    val categoryId: Long,
    val venue: String,
)

@Serializable
class ParticipantPostDto(
    val personId: Long,
    val rank: Int? = null,
    val score: String? = null,
)