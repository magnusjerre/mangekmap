package com.experis.mangekamp.controllers.seasons

import com.experis.mangekamp.controllers.categories.toDto
import com.experis.mangekamp.controllers.persons.toDto
import com.experis.mangekamp.logic.SeasonParticipant
import com.experis.mangekamp.logic.SeasonSimplifiedEvent
import com.experis.mangekamp.logic.calculateSeason
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Gender
import com.experis.mangekamp.models.Region
import com.experis.mangekamp.models.Season
import dto.EventResultDto
import dto.RegionDto
import dto.SeasonDto
import dto.SeasonParticipantDto
import dto.SeasonPostDto
import dto.SimpleEventDto

fun Season.toDto(excludeEvents: Boolean = false): SeasonDto {
//    val malesResult =
//        events.calculateSeason(seasonId = id!!, gender = Gender.MALE, expectedMangekjemperEvents = mangekjemperRequiredEvents.toInt())
//    val femalesResult =
//        events.calculateSeason(seasonId = id!!, gender = Gender.FEMALE, expectedMangekjemperEvents = mangekjemperRequiredEvents.toInt())

    return SeasonDto(
        events = if (excludeEvents) emptyList() else events.sortedBy { it.date }.map(Event::toDtoSimple),
        participants = emptyList(),//malesResult + femalesResult).map(SeasonParticipant::toDto),
        name = name,
        startYear = startYear,
        mangekjemperRequiredEvents = mangekjemperRequiredEvents,
        id = id
    )
}

fun Season.toDto2(allEvents: List<Event>): SeasonDto {
    val malesResult = allEvents.calculateSeason(
        seasonId = id!!,
        gender = Gender.MALE,
        expectedMangekjemperEvents = mangekjemperRequiredEvents.toInt()
    )
    val femalesResult = allEvents.calculateSeason(
        seasonId = id!!,
        gender = Gender.FEMALE,
        expectedMangekjemperEvents = mangekjemperRequiredEvents.toInt()
    )

    return SeasonDto(
        events = events.sortedBy { it.date }.map(Event::toDtoSimple),
        participants = (malesResult + femalesResult).map(SeasonParticipant::toDto),
        name = name,
        startYear = startYear,
        mangekjemperRequiredEvents = mangekjemperRequiredEvents,
        id = id
    )
}

fun Event.toDtoSimple(): SimpleEventDto = SimpleEventDto(
    id = id!!,
    name = title,
    categoryDto = category.toDto()
)

fun SeasonParticipant.toDto(): SeasonParticipantDto = SeasonParticipantDto(
    personId = personId,
    name = personName,
    gender = gender.toDto(),
    seasonRank = seasonRank,
    seasonPoints = seasonPoints,
    isMangekjemper = isMangekjemper,
    results = this.events.map(SeasonSimplifiedEvent::toDto)
)

fun SeasonSimplifiedEvent.toDto(): EventResultDto = EventResultDto(
    eventId = eventId,
    actualRank = actualRank,
    isAttendanceOnly = isAttendanceOnly,
    mangekjemperRank = mangekjemperRank
)

fun SeasonPostDto.toModel(): Season = Season(
    events = mutableListOf(),
    name = name,
    startYear = startYear,
    mangekjemperRequiredEvents = mangekjemperRequiredEvents,
//    region = when(region) {
//        RegionDto.OSLO -> Region.OSLO
//        RegionDto.TRONDHEIM -> Region.TRONDHEIM
//        RegionDto.BERGEN -> Region.BERGEN
//    }
)