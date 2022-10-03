package dto

import kotlinx.serialization.Serializable

@Serializable
class CategoryDto(val name: String, val color: String, val id: Long)