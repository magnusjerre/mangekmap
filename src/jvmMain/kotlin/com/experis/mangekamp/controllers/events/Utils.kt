package com.experis.mangekamp.controllers.events

import com.experis.mangekamp.controllers.categories.toDto
import com.experis.mangekamp.controllers.persons.toDto
import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Participant
import dto.EventDto
import dto.EventPostDto
import dto.ParticipantDto
import dto.ParticipantSimpleDto
import dto.RegionDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun Event.toDto(includeParticipants: Boolean = true): EventDto = EventDto(
    date = date.format(DateTimeFormatter.ISO_DATE),
    title = title,
    category = category.toDto(),
    venue = venue,
    isTeamBased = isTeamBased,
    participants = if (includeParticipants) participants.map(Participant::toDto) else emptyList(),
    id = id!!,
    seasonId = season.id!!
)

fun Participant.toDto(): ParticipantDto = ParticipantDto(
    rank = rank,
    isAttendanceOnly = isAttendanceOnly,
    score = score,
    name = id.person.name,
    gender = id.person.gender.toDto(),
    personId = id.person.id!!,
    teamNumber = teamNumber
)

fun EventPostDto.toModel(categoryIdMapper: (Long) -> Category): Event = Event(
    date = LocalDate.parse(date, DateTimeFormatter.ISO_DATE),
    title = title,
    category = categoryIdMapper(categoryId),
    venue = venue,
    isTeamBased = isTeamBased,
    participants = emptyList()
)

fun Participant.toParticipantSimpleDto(): ParticipantSimpleDto = ParticipantSimpleDto(
    eventId = id.event.id!!,
    eventTitle = id.event.title,
    eventDate = id.event.date.format(DateTimeFormatter.ISO_DATE),
    categoryDto = id.event.category.toDto(),
    rank = rank,
    isAttendanceOnly = isAttendanceOnly,
    score = score,
    seasonId = id.event.season.id!!,
    seasonName = id.event.season.name,
    region = RegionDto.valueOf(id.event.season.region.name)
)