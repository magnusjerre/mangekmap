package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Gender

internal fun List<Event>.toSeasonParticipants(gender: Gender): List<SeasonParticipant> {
    return flatMap { it.participants }
        .filter { it.id.person.gender == gender }
        .groupBy { it.id.person }
        .map { (person, personParticipations) ->
            SeasonParticipant(
                personId = person.id!!.toLong(),
                personName = person.name,
                gender = person.gender,
                seasonRank = -1,
                seasonPoints = -1,
                eventParticipations = personParticipations.map { pt ->
                    EventParticipation(
                        eventName = pt.id.event.title,
                        category = pt.id.event.category,
                        actualRank = pt.rank,
                        isAttendanceOnly = pt.isAttendanceOnly,
                        mangekjemperRank = null,
                        eventId = pt.id.event.id!!,
                        seasonId = pt.id.event.season.id!!,
                        isTeamBased = pt.id.event.isTeamBased,
                        teamNumber = pt.teamNumber
                    )
                }
            )
        }
}