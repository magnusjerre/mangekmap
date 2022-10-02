package dto

import kotlinx.serialization.Serializable

@Serializable
data class PersonDto(val name: String, val email: String, val gender: GenderDto, val retired: Boolean, val id: Long? = null)

@Serializable
enum class GenderDto {
    MALE, FEMALE
}
