package com.experis.mangekamp.controllers.seasons

import com.experis.mangekamp.exceptions.ResourceNotFoundException
import com.experis.mangekamp.repositories.SeasonRepository
import dto.SeasonDto
import dto.SeasonPostDto
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/seasons")
class SeasonsController(
    private val seasonRepository: SeasonRepository
) {

    @GetMapping
    fun getSeasons(): List<SeasonDto> = seasonRepository.findAll().map { it.toDto() }

    @GetMapping("{id}")
    fun getSeason(@PathVariable id: Long, @RequestParam excludeEvents: Boolean = false): SeasonDto =
        seasonRepository.findById(id).orElseThrow { ResourceNotFoundException("Season with id $id not found") }.toDto()

    @PostMapping
    fun postSeason(@RequestBody seasonDto: SeasonPostDto): SeasonDto {
        return seasonRepository.save(seasonDto.toModel()).toDto()
    }

    @PatchMapping("{id}")
    fun patchSeason(@PathVariable id: Long, @RequestBody seasonDto: SeasonPostDto): SeasonDto {
        val season = seasonRepository.findById(id).orElseThrow { ResourceNotFoundException("Season with id $id not foun") }

        season.name = seasonDto.name
        season.startYear = seasonDto.startYear

        return seasonRepository.save(season).toDto()
    }

    @DeleteMapping("{id}")
    fun deleleteSeason(@PathVariable id: Long) {
        seasonRepository.deleteById(id)
    }
}