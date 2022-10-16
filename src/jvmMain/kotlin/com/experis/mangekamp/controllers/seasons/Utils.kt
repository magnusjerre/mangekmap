package com.experis.mangekamp.controllers.seasons

import com.experis.mangekamp.controllers.categories.toDto
import com.experis.mangekamp.controllers.persons.toDto
import com.experis.mangekamp.logic.SeasonParticipant
import com.experis.mangekamp.logic.SeasonSimplifiedEvent
import com.experis.mangekamp.logic.calculateSeason
import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Gender
import com.experis.mangekamp.models.Season
import dto.EventPostDto
import dto.EventResult
import dto.SeasonDto
import dto.SeasonParticipantDto
import dto.SeasonPostDto
import dto.SimpleEventDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun Season.toDto(excludeEvents: Boolean = false): SeasonDto {
    val malesResult = events.calculateSeason(gender = Gender.MALE)
    val femalesResult = events.calculateSeason(gender = Gender.FEMALE)

    return SeasonDto(
        events = if (excludeEvents) emptyList() else events.sortedBy { it.date }.map(Event::toDtoSimple),
        participants = (malesResult + femalesResult).map(SeasonParticipant::toDto),
        name = name,
        startYear = startYear,
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

fun SeasonSimplifiedEvent.toDto(): EventResult = EventResult(
    eventId = eventId,
    actualRank = actualRank,
    mangekjemperRank = mangekjemperRank
)

fun SeasonPostDto.toModel(): Season = Season(
    events = mutableListOf(),
    name = name,
    startYear = startYear
)