package com.experis.mangekamp.logic

import java.lang.Integer.max

class EventPointsCalculator(
    val maxEventsOfSameBeforePenalty: Int,
    val currentSeasonId: Long,
    val totalMangekjempere: Int,
    val penaltyPoints: Int
) {
    private var count: Int = 0

    fun calculateAndSetEventPoints(eventParticipation: EventParticipation): EventParticipation = eventParticipation.apply {
        if (eventParticipation.mangekjemperRank != null) {
            calculateAndSetMangekjemperPoints(eventParticipation)
        } else {
            calculateAndSetNonMangekjemperPoints(eventParticipation)
        }
    }

    private fun calculateAndSetMangekjemperPoints(eventParticipation: EventParticipation) {
        if (++count <= maxEventsOfSameBeforePenalty) {
            if (eventParticipation.seasonId == currentSeasonId) {
                eventParticipation.eventPoints = eventParticipation.mangekjemperRank!!
                eventParticipation.eventPointsReason = PointsReason.MANGEKJEMPER
            } else {
                eventParticipation.eventPoints = totalMangekjempere
                eventParticipation.eventPointsReason = PointsReason.OTHER_REGION_MANGEKJEMPER
            }
        } else {
            eventParticipation.eventPoints = max(penaltyPoints, eventParticipation.mangekjemperRank!!)
            eventParticipation.eventPointsReason = PointsReason.MANGEKJEMPER_TOO_MANY_OF_SAME
        }
    }

    private fun calculateAndSetNonMangekjemperPoints(eventParticipation: EventParticipation) {
        if (eventParticipation.seasonId == currentSeasonId) {
            eventParticipation.eventPoints = eventParticipation.actualRank!!
            eventParticipation.eventPointsReason = PointsReason.NOT_MANGEKJEMPER
        } else {
            eventParticipation.eventPoints = max(eventParticipation.actualRank!!, penaltyPoints)
            eventParticipation.eventPointsReason = PointsReason.OTHER_REGION_NOT_MANGEKJEMPER
        }
    }
}