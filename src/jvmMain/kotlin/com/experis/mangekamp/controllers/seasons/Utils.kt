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
import dto.EventPointsReasonDto
import dto.EventResultDto
import dto.RegionDto
import dto.SeasonDto
import dto.SeasonParticipantDto
import dto.SeasonPenaltyPointsDto
import dto.SeasonPostDto
import dto.SimpleEventDto

fun Season.toDto(allEvents: List<Event>): SeasonDto {
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
        region = when (this.region) {
            Region.BERGEN -> RegionDto.BERGEN
            Region.OSLO -> RegionDto.OSLO
            Region.TRONDHEIM -> RegionDto.TRONDHEIM
        },
        id = id
    )
}

fun Season.toDtoSimple(): SeasonDto {
    return SeasonDto(
        events = emptyList(),
        participants = emptyList(),
        name = name,
        startYear = startYear,
        mangekjemperRequiredEvents = mangekjemperRequiredEvents,
        region = when (this.region) {
            Region.BERGEN -> RegionDto.BERGEN
            Region.OSLO -> RegionDto.OSLO
            Region.TRONDHEIM -> RegionDto.TRONDHEIM
        },
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
    seasonPenaltyPoints = seasonPenaltyPoints?.let { SeasonPenaltyPointsDto(it.pointsPerMissingEvent, it.numberOfMissingEvents) },
    isMangekjemper = isMangekjemper,
    results = this.events.map(SeasonSimplifiedEvent::toDto)
)

fun SeasonSimplifiedEvent.toDto(): EventResultDto = EventResultDto(
    eventId = eventId,
    seasonId = seasonId,
    actualRank = actualRank,
    eventPoints = eventPoints,
    eventCategoryName = category.name,
    eventPointsReason = eventPointsReason?.let { d -> EventPointsReasonDto.values().find { it.ordinal == d.ordinal } },
    isAttendanceOnly = isAttendanceOnly,
    mangekjemperRank = mangekjemperRank
)

fun SeasonPostDto.toModel(): Season = Season(
    events = mutableListOf(),
    name = name,
    startYear = startYear,
    mangekjemperRequiredEvents = mangekjemperRequiredEvents,
    region = region.toModel()
)

fun RegionDto.toModel(): Region = when (this) {
    RegionDto.TRONDHEIM -> Region.TRONDHEIM
    RegionDto.BERGEN -> Region.BERGEN
    RegionDto.OSLO -> Region.OSLO
}