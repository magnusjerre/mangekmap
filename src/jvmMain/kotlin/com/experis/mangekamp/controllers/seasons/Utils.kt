package com.experis.mangekamp.controllers.seasons

import com.experis.mangekamp.controllers.events.toDto
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Season
import dto.EventDto
import dto.SeasonDto
import dto.SeasonPostDto
import java.time.format.DateTimeFormatter

fun Season.toDto(excludeEvents: Boolean = false): SeasonDto = SeasonDto(
    events = if (excludeEvents) emptyList() else events.map { it.toDto(includeParticipants = false) },
    name = name,
    startYear = startYear,
    id = id
)

fun SeasonPostDto.toModel(): Season = Season(
    events = emptyList(),
    name = name,
    startYear = startYear
)