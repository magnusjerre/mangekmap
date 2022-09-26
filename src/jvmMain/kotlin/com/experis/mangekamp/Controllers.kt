package com.experis.mangekamp

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("public")
class PublicStuffController {

    @GetMapping
    fun yo() = "yo"

}

@RestController
@RequestMapping("privatestuff")
class PrivateStuffController {
    @GetMapping
    fun nono() = "nono"
}