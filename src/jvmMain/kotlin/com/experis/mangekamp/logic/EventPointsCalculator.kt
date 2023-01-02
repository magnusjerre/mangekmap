package com.experis.mangekamp.logic

import java.lang.Integer.max

class EventPointsCalculator(
    val maxEventsOfSameBeforePenalty: Int,
    val currentSeasonId: Long,
    val totalMangekjempere: Int,
    val penaltyPoints: Int
) {
    private var count: Int = 0

    fun calculateAndSetEventPoints(event: SeasonSimplifiedEvent): SeasonSimplifiedEvent = event.apply {
        if (event.mangekjemperRank != null) {
            calculateAndSetMangekjemperPoints(event)
        } else {
            calculateAndSetNonMangekjemperPoints(event)
        }
    }

    private fun calculateAndSetMangekjemperPoints(event: SeasonSimplifiedEvent) {
        if (++count <= maxEventsOfSameBeforePenalty) {
            if (event.seasonId == currentSeasonId) {
                event.eventPoints = event.mangekjemperRank!!
                event.eventPointsReason = PointsReason.MANGEKJEMPER
            } else {
                event.eventPoints = totalMangekjempere
                event.eventPointsReason = PointsReason.OTHER_REGION_MANGEKJEMPER
            }
        } else {
            event.eventPoints = max(penaltyPoints, event.mangekjemperRank!!)
            event.eventPointsReason = PointsReason.MANGEKJEMPER_TOO_MANY_OF_SAME
        }
    }

    private fun calculateAndSetNonMangekjemperPoints(event: SeasonSimplifiedEvent) {
        if (event.seasonId == currentSeasonId) {
            event.eventPoints = event.actualRank!!
            event.eventPointsReason = PointsReason.NOT_MANGEKJEMPER
        } else {
            event.eventPoints = max(event.actualRank!!, penaltyPoints)
            event.eventPointsReason = PointsReason.OTHER_REGION_NOT_MANGEKJEMPER
        }
    }
}