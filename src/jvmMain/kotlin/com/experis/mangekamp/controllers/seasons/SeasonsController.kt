package com.experis.mangekamp.controllers.seasons

import com.experis.mangekamp.controllers.events.toDto
import com.experis.mangekamp.controllers.events.toModel
import com.experis.mangekamp.exceptions.ResourceNotFoundException
import com.experis.mangekamp.repositories.CategoryRepository
import com.experis.mangekamp.repositories.EventRepository
import com.experis.mangekamp.repositories.ParticipantRepository
import com.experis.mangekamp.repositories.SeasonRepository
import dto.EventDto
import dto.EventPostDto
import dto.SeasonDto
import dto.SeasonPostDto
import org.springframework.data.repository.findByIdOrNull
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
    private val eventRepository: EventRepository,
    private val categoryRepository: CategoryRepository,
    private val seasonRepository: SeasonRepository,
    private val participantRepository: ParticipantRepository
) {

    @GetMapping
    fun getSeasons(): List<SeasonDto> = seasonRepository.findAll().map { it.toDto() }

    @GetMapping("{id}")
    fun getSeason(@PathVariable id: Long, @RequestParam excludeEvents: Boolean = false): SeasonDto {
        val season = seasonRepository.findById(id).orElseThrow { ResourceNotFoundException("Season with id $id not found") }
        val uniqueParticipantsForSeason = season.events.flatMap { it.participants }.map { it.id.person.id!! }.distinct()
        val allParticipants = uniqueParticipantsForSeason.map { participantRepository.findAllByIdPersonIdAndIdEventSeasonStartYear(it, season.startYear) }.flatten()
        val allEvents = allParticipants.map { it.id.event }.distinctBy { it.id }
        allEvents.forEach { it.participants = it.participants.filter { p -> uniqueParticipantsForSeason.contains(p.id.person.id!!) } }
        return season.toDto2(allEvents)
    }

    @PostMapping
    fun postSeason(@RequestBody seasonDto: SeasonPostDto): SeasonDto {
        return seasonRepository.save(seasonDto.toModel()).toDto()
    }

    @PatchMapping("{id}")
    fun patchSeason(@PathVariable id: Long, @RequestBody seasonDto: SeasonPostDto): SeasonDto {
        val season =
            seasonRepository.findById(id).orElseThrow { ResourceNotFoundException("Season with id $id not foun") }

        season.name = seasonDto.name
        season.startYear = seasonDto.startYear
        season.mangekjemperRequiredEvents = seasonDto.mangekjemperRequiredEvents

        return seasonRepository.save(season).toDto()
    }

    @PostMapping("{id}/events")
    fun postSeasonEvent(@PathVariable id: Long, @RequestBody event: EventPostDto): EventDto {
        val season =
            seasonRepository.findByIdOrNull(id) ?: throw ResourceNotFoundException("Season with id $id not found")
        val category = categoryRepository.findByIdOrNull(event.categoryId)
            ?: throw ResourceNotFoundException("Category with id ${event.categoryId} not found")
        val eventToSave = event.toModel { category }.apply { this.season = season }
        return eventRepository.save(eventToSave).toDto()
    }

    @DeleteMapping("{id}")
    fun deleleteSeason(@PathVariable id: Long) {
        seasonRepository.deleteById(id)
    }
}