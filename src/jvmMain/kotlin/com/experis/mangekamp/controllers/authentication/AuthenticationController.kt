package com.experis.mangekamp.controllers.authentication

import ApiAuthentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationController {
    // Hacky-wacky løsning frem til ordentlig autentisering er på plass
    @GetMapping(ApiAuthentication.BASE_PATH)
    fun doNothing() {

    }
}