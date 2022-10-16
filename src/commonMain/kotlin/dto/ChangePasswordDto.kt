package dto

import kotlinx.serialization.Serializable

@Serializable
class ChangePasswordDto(val oldPassword: String, val newPassword: String)