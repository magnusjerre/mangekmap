package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Category
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class EventPointsCalculatorTest {
    @Test
    fun `calculateAndSetEventPoints should use the mangekjemperRank when fewer than the maxEventsOfSameBeforePenalty is met`() {
        val eventPointsCalculator = EventPointsCalculator(
            maxEventsOfSameBeforePenalty = 3,
            currentSeasonId = 1,
            totalMangekjempere = 5,
            penaltyPoints = 10
        )
        eventPointsCalculator.calculateAndSetEventPoints(eventMangekjemper(1, 1L)).should {
            it.eventPoints.shouldBe(1)
            it.eventPointsReason.shouldBe(PointsReason.MANGEKJEMPER)
        }
        eventPointsCalculator.calculateAndSetEventPoints(eventMangekjemper(1, 1L)).should {
            it.eventPoints.shouldBe(1)
            it.eventPointsReason.shouldBe(PointsReason.MANGEKJEMPER)
        }
        eventPointsCalculator.calculateAndSetEventPoints(eventMangekjemper(2, 1L)).should {
            it.eventPoints.shouldBe(2)
            it.eventPointsReason.shouldBe(PointsReason.MANGEKJEMPER)
        }
        eventPointsCalculator.calculateAndSetEventPoints(eventMangekjemper(3, 1L)).should {
            it.eventPoints.shouldNotBe(3)
            it.eventPointsReason.shouldNotBe(PointsReason.MANGEKJEMPER)
        }
    }

    @Test
    fun `calculateAndSetEventPoints should use the totalMangekjempere value for event from other season when fewer than the maxEventsOfSameBeforePenalty `() {
        val eventPointsCalculator = EventPointsCalculator(
            maxEventsOfSameBeforePenalty = 3,
            currentSeasonId = 1,
            totalMangekjempere = 5,
            penaltyPoints = 10
        )
        eventPointsCalculator.calculateAndSetEventPoints(eventMangekjemper(1, 13L)).should {
            it.eventPoints.shouldBe(5)
            it.eventPointsReason.shouldBe(PointsReason.OTHER_REGION_MANGEKJEMPER)
        }
    }

    @Test
    fun `calculateAndSetEventPoints should use the max of penaltyPoints and mangekjemperRank when more than maxEventsOfSameBeforePenalty have been attended`() {
        val eventPointsCalculator = EventPointsCalculator(
            maxEventsOfSameBeforePenalty = 0,
            currentSeasonId = 1,
            totalMangekjempere = 5,
            penaltyPoints = 10
        )
        eventPointsCalculator.calculateAndSetEventPoints(eventMangekjemper(1, 1L)).should {
            it.eventPoints.shouldBe(10)
            it.eventPointsReason.shouldBe(PointsReason.MANGEKJEMPER_TOO_MANY_OF_SAME)
        }
        eventPointsCalculator.calculateAndSetEventPoints(eventMangekjemper(22, 1L)).should {
            it.eventPoints.shouldBe(22)
            it.eventPointsReason.shouldBe(PointsReason.MANGEKJEMPER_TOO_MANY_OF_SAME)
        }
    }

    @Test
    fun `calculateAndSetEventPoints non-mangekjemper should use the actualRank for event in this season`() {
        val eventPointsCalculator = EventPointsCalculator(
            maxEventsOfSameBeforePenalty = 3,
            currentSeasonId = 1,
            totalMangekjempere = 5,
            penaltyPoints = 10
        )
        eventPointsCalculator.calculateAndSetEventPoints(eventNotMangekjemper(7, 1L)).should {
            it.eventPoints.shouldBe(7)
            it.eventPointsReason.shouldBe(PointsReason.NOT_MANGEKJEMPER)
        }
    }

    @Test
    fun `calculateAndSetEventPoints non-mangekjemper should use the max of actualRank and penaltyPoints for event in other season`() {
        val eventPointsCalculator = EventPointsCalculator(
            maxEventsOfSameBeforePenalty = 3,
            currentSeasonId = 1,
            totalMangekjempere = 5,
            penaltyPoints = 10
        )
        eventPointsCalculator.calculateAndSetEventPoints(eventNotMangekjemper(7, 2L)).should {
            it.eventPoints.shouldBe(10)
            it.eventPointsReason.shouldBe(PointsReason.OTHER_REGION_NOT_MANGEKJEMPER)
        }
        eventPointsCalculator.calculateAndSetEventPoints(eventNotMangekjemper(14, 2L)).should {
            it.eventPoints.shouldBe(14)
            it.eventPointsReason.shouldBe(PointsReason.OTHER_REGION_NOT_MANGEKJEMPER)
        }
    }

    private fun eventMangekjemper(mangekjemperRank: Int, seasonId: Long) = EventParticipation(
        eventName = "generic",
        category = Category("", ""),
        eventId = 1L,
        mangekjemperRank = mangekjemperRank,
        seasonId = seasonId
    )

    private fun eventNotMangekjemper(actualRank: Int, seasonId: Long) = EventParticipation(
        eventName = "generic",
        category = Category("", ""),
        eventId = 1L,
        actualRank = actualRank,
        seasonId = seasonId
    )
}