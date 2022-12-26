package com.experis.mangekamp.controllers.seasons

import ApiSeasons
import com.experis.mangekamp.controllers.events.toDto
import com.experis.mangekamp.controllers.events.toModel
import com.experis.mangekamp.exceptions.ResourceNotFoundException
import com.experis.mangekamp.logPerformance
import com.experis.mangekamp.repositories.CategoryRepository
import com.experis.mangekamp.repositories.EventRepository
import com.experis.mangekamp.repositories.ParticipantRepository
import com.experis.mangekamp.repositories.SeasonRepository
import dto.EventDto
import dto.EventPostDto
import dto.SeasonDto
import dto.SeasonPostDto
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SeasonsController(
    private val eventRepository: EventRepository,
    private val categoryRepository: CategoryRepository,
    private val seasonRepository: SeasonRepository,
    private val participantRepository: ParticipantRepository
) {

    private val logger = LoggerFactory.getLogger(SeasonsController::class.java)

    @GetMapping(ApiSeasons.BASE_PATH)
    fun getSeasons(): List<SeasonDto> = seasonRepository.findAll().map { it.toDtoSimple() }

    @PostMapping(ApiSeasons.BASE_PATH)
    fun postSeason(@RequestBody seasonDto: SeasonPostDto): SeasonDto {
        return seasonRepository.save(seasonDto.toModel()).let { it.toDto(it.events) }
    }

    @GetMapping(ApiSeasons.ID)
    fun getSeason(@PathVariable id: Long, @RequestParam excludeEvents: Boolean = false): SeasonDto {
        val startTime = System.currentTimeMillis()
        val season =
            seasonRepository.findById(id).orElseThrow { ResourceNotFoundException("Season with id $id not found") }
        val uniqueParticipantsForSeason = season.events.flatMap { it.participants }.map { it.id.person.id!! }.distinct()
        val time = logger.logPerformance(
            startTime,
            "getSeasons() - fetch season and fond ${uniqueParticipantsForSeason.count()} unique participants"
        )
        val participantsAlsoInOtherRegions =
            participantRepository.findAllByIdPersonIdInAndIdEventSeasonStartYearAndIdEventSeasonIdIsNot(
                uniqueParticipantsForSeason,
                season.startYear,
                id
            )
        val eventsFromOtherRegions = participantsAlsoInOtherRegions.map { it.id.event }.distinctBy { it.id }
        val time2 = logger.logPerformance(
            time,
            "getSeasons() - fetching all participants for ${uniqueParticipantsForSeason.count()} unique participants"
        )
        val allEvents =
            season.events.flatMap { it.participants }.map { it.id.event }.distinctBy { it.id } + eventsFromOtherRegions
        val time3 = logger.logPerformance(time2, "getSeasons() - allEventsFilter")
        val output = season.toDto(allEvents)
        logger.logPerformance(time3, "getSeasons() - dto mapping and season calculation")
        logger.logPerformance(startTime, "getSeasons() - total processing time")
        return output
    }

    @DeleteMapping(ApiSeasons.ID)
    fun deleteSeason(@PathVariable id: Long) {
        seasonRepository.deleteById(id)
    }

    @PatchMapping(ApiSeasons.ID)
    fun patchSeason(@PathVariable id: Long, @RequestBody seasonDto: SeasonPostDto): SeasonDto {
        val season =
            seasonRepository.findById(id).orElseThrow { ResourceNotFoundException("Season with id $id not foun") }

        season.name = seasonDto.name
        season.startYear = seasonDto.startYear
        season.mangekjemperRequiredEvents = seasonDto.mangekjemperRequiredEvents
        season.region = seasonDto.region.toModel()

        return seasonRepository.save(season).toDto(season.events)
    }

    @PostMapping(ApiSeasons.ID_EVENTS)
    fun postSeasonEvent(@PathVariable id: Long, @RequestBody event: EventPostDto): EventDto {
        val season =
            seasonRepository.findByIdOrNull(id) ?: throw ResourceNotFoundException("Season with id $id not found")
        val category = categoryRepository.findByIdOrNull(event.categoryId)
            ?: throw ResourceNotFoundException("Category with id ${event.categoryId} not found")
        val eventToSave = event.toModel { category }.apply { this.season = season }
        return eventRepository.save(eventToSave).toDto()
    }
}