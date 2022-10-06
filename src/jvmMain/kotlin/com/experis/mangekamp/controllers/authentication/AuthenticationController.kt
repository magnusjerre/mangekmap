package com.experis.mangekamp.controllers.authentication

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("check_authentication")
class AuthenticationController {
    // Hacky-wacky løsning frem til ordentlig autentisering er på plass
    @GetMapping
    fun doNothing() {

    }
}