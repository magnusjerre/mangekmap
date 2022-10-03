package dto

import kotlinx.serialization.Serializable

@Serializable
data class SeasonDto(
    var events: List<EventDto>,
    var name: String,
    var startYear: Int,
    var id: Long? = null
)

@Serializable
class SeasonPostDto(
    var name: String,
    var startYear: Int
)