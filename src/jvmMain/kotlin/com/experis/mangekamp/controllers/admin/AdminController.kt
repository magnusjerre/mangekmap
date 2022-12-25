package com.experis.mangekamp.controllers.admin

import ApiAdmin
import dto.ChangePasswordDto
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminController(
    private val userDetailsManager: UserDetailsManager
) {
    @PutMapping(ApiAdmin.BASE_PATH)
    fun changePassword(@RequestBody dto: ChangePasswordDto) {
        userDetailsManager.changePassword(dto.oldPassword, dto.newPassword)
    }
}