package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Gender
import com.experis.mangekamp.models.Region
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class FinalScoreCalculation {
    @Test
    fun `Should calculate mangekjemper rank for attendance only using the total number of mangekjempere`() {
        val season = setupSeason(
            name = "2022-2023",
            region = Region.OSLO,
            mangekjemperRequiredEvents = 4,
            persons = listOf(
                DONALD_DUCK,
                OLE,
                DOLE,
                DOFFEN,
                ONKEL_SKRUE,
            ),
            events = listOf(
                SetupEvent("Minigolf", SetupCategory.BALL)
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2)
                    .add(ONKEL_SKRUE, 3)
                    .add(DOLE, 4)
                    .add(DOFFEN, 5),
                SetupEvent("Orientering", SetupCategory.KONDISJON)
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2)
                    .add(ONKEL_SKRUE, 3)
                    .add(DOLE, 4)
                    .add(DOFFEN, 5),
                SetupEvent("Bordtennis", SetupCategory.BALL)
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2)
                    .add(ONKEL_SKRUE, 3)
                    .add(DOLE, 4)
                    .add(DOFFEN, 5),
                SetupEvent("Frisbeegolf", SetupCategory.TEKNIKK)
                    .add(DONALD_DUCK, 1)
                    .add(ONKEL_SKRUE, 2)
                    .add(DOLE, 3)
                    .addAttendanceOnly(DOFFEN),
                SetupEvent("Roing", SetupCategory.KONDISJON)
                    .add(DONALD_DUCK, 1)
                    .addAttendanceOnly(DOLE)
            )
        )

        val result = season.events.calculateSeason(
            seasonId = season.id!!,
            gender = Gender.MALE,
            expectedMangekjemperEvents = season.mangekjemperRequiredEvents.toInt()
        ) { it.events.isMangekjemper(season.mangekjemperRequiredEvents.toInt())}
        val winner = result[0]
        winner.shouldHave(name = "Donald Duck", seasonRank = 1, seasonPoints = 4, mangekjemperStatus = true)
        winner.shouldHaveMangekjemperRanks(listOf(1.b, 1.k, 1.b, 1.t, 1.t), season.events)
        val second = result[1]
        second.shouldHave(name = "Onkel Skrue", seasonRank = 2, seasonPoints = 8, mangekjemperStatus = true)
        second.shouldHaveMangekjemperRanks(listOf(2.b, 2.k, 2.b, 2.t, 0.t), season.events)
        val third = result[2]
        third.shouldHave(name = "Dole", seasonRank = 3, seasonPoints = 12, mangekjemperStatus = true)
        third.shouldHaveMangekjemperRanks(listOf(3.b, 3.k, 3.b, 3.t, 4.t), season.events)
        val fourth = result[3]
        fourth.shouldHave(name = "Doffen", seasonRank = 4, 16, mangekjemperStatus = true)
        fourth.shouldHaveMangekjemperRanks(listOf(4.b, 4.k, 4.b, 4.t, 0.t), season.events)
        val fifth = result[4]
        fifth.shouldHave(name = "Ole", seasonRank = 5, seasonPoints = 10, mangekjemperStatus = false)
        fifth.shouldHaveMangekjemperRanks(listOf(0.b, 0.k, 0.b, 0.t, 0.t), season.events)
    }

    @Test
    fun `Should correctly calculate season`() {
        val season = setupSeason(
            name = "2022-2023",
            region = Region.OSLO,
            mangekjemperRequiredEvents = 8,
            persons = listOf(
                DONALD_DUCK,
                OLE,
                DOLE,
                DOFFEN,
                ONKEL_SKRUE,
            ),
            events = listOf(
                SetupEvent("Minigolf", SetupCategory.BALL)
                    .add(DONALD_DUCK, 1)
                    .add(ONKEL_SKRUE, 2)
                    .add(DOFFEN, 3)
                    .add(OLE, 4)
                    .add(DOLE, 5),
                SetupEvent("Orientering", SetupCategory.KONDISJON)
                    .add(ONKEL_SKRUE, 1)
                    .add(DONALD_DUCK, 2)
                    .add(OLE, 3)
                    .add(DOFFEN, 4),
                SetupEvent("Bordtennis", SetupCategory.BALL)
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2)
                    .add(DOFFEN, 3)
                    .add(DOLE, 4),
                SetupEvent("Frisbeegolf", SetupCategory.TEKNIKK)
                    .add(DOFFEN, 1,)
                    .add(DONALD_DUCK, 2)
                    .add(OLE, 3)
                    .add(DOLE, 4),
                SetupEvent("Roing", SetupCategory.KONDISJON)
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2)
                    .add(ONKEL_SKRUE, 3)
                    .add(DOFFEN, 4)
                    .add(DOLE, 5),
                SetupEvent("Poker", SetupCategory.TEKNIKK)
                    .add(OLE, 1)
                    .add(DONALD_DUCK, 2)
                    .add(DOFFEN, 3)
                    .add(DOLE, 4),
                SetupEvent("E-sport", SetupCategory.TEKNIKK)
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2)
                    .add(DOFFEN, 3)
                    .add(DOLE, 4),
                SetupEvent("Tennis Double", SetupCategory.BALL)
                    .addTeam(1, null, DONALD_DUCK, DOFFEN)
                    .addTeam(2, null, OLE, ONKEL_SKRUE)
                    .addTeam(3, null, DOLE),
                SetupEvent("Padel", SetupCategory.BALL)
                    .add(DONALD_DUCK, 1)
                    .add(DOFFEN, 2)
                    .add(OLE, 3)
                    .add(DOLE, 4)
            )
        )

        val result = season.events.calculateSeason(
            seasonId = season.id!!,
            gender = Gender.MALE,
            expectedMangekjemperEvents = season.mangekjemperRequiredEvents.toInt()
        ) { it.events.isMangekjemper(season.mangekjemperRequiredEvents.toInt())}

        val winner = result[0]
        winner.shouldHave(name = "Donald Duck", seasonRank = 1, seasonPoints = 10, mangekjemperStatus = true)
        winner.shouldHaveMangekjemperRanks(listOf(1.b, 1.k, 1.b, 2.t, 1.k, 2.t, 1.t, 1.b, 1.b), season.events)
        val second = result[1]
        second.shouldHave(name = "Ole", seasonRank = 2, seasonPoints = 17, mangekjemperStatus = true)
        second.shouldHaveMangekjemperRanks(listOf(3.b, 2.k, 2.b, 3.t, 2.k, 1.t, 2.t, 2.b, 3.b), season.events)
        val third = result[2]
        third.shouldHave(name = "Doffen", seasonRank = 3, seasonPoints = 18, mangekjemperStatus = true)
        third.shouldHaveMangekjemperRanks(listOf(2.b, 3.k, 3.b, 1.t, 3.k, 3.t, 3.t, 1.b, 2.b), season.events)
        val fourth = result[3]
        fourth.shouldHave(name = "Dole", seasonRank = 4, seasonPoints = 43, mangekjemperStatus = true)
        fourth.shouldHaveMangekjemperRanks(listOf(4.b, 0.k, 4.b, 4.t, 4.k, 4.t, 4.t, 3.b, 4.b), season.events)
        val last = result[4]
        last.shouldHave(name = "Onkel Skrue", seasonRank = 5, seasonPoints = 40, mangekjemperStatus = false)
        last.shouldHaveMangekjemperRanks(listOf(0.b, 0.k, 0.b, 0.t, 0.k, 0.t, 0.t, 0.b, 0.b), season.events)
    }

    @Test
    fun `Should use participation from other regions when assessing mangekjemper status and when calculating their total score`() {
        val persons = listOf(
            DONALD_DUCK,
            OLE,
            DOLE,
            DOFFEN
        ).toActualPersons()
        var id = 1L
        val idGenerator: () -> Long = {
            id++
        }
        val season = setupSeasonV2(
            id = 1,
            name = "season",
            region = Region.OSLO,
            mangekjemperRequiredEvents = 4,
            actualPersons = persons,
            eventIdGenerator = idGenerator,
            events = listOf(
                SetupEvent("Minigolf", SetupCategory.BALL)
                    .add(DONALD_DUCK, 1),
                SetupEvent("Orientering", SetupCategory.KONDISJON)
                    .add(DONALD_DUCK, 1)
                    .add(DOLE, 1)
                    .add(OLE, 3)
                    .add(DOFFEN, 3),
                SetupEvent("Bordtennis", SetupCategory.BALL)
                    .add(DONALD_DUCK, 1)
                    .add(DOFFEN, 2),
                SetupEvent("Frisbeegolf", SetupCategory.TEKNIKK)
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2),
            )
        )

        val seasonOtherRegion1 = setupSeasonV2(
            id = 2,
            name = "season other 1",
            region = Region.TRONDHEIM,
            mangekjemperRequiredEvents = 4,
            actualPersons = persons,
            eventIdGenerator = idGenerator,
            events = listOf(
                SetupEvent("Roing", SetupCategory.KONDISJON)
                    .add(DOLE, 1)
                    .add(OLE, 2),
                SetupEvent("Poker", SetupCategory.TEKNIKK)
                    .add(DOLE, 1),
            )
        )

        val seasonOtherRegion2 = setupSeasonV2(
            id = 3,
            name = "season other 2",
            region = Region.BERGEN,
            mangekjemperRequiredEvents = 4,
            actualPersons = persons,
            eventIdGenerator = idGenerator,
            events = listOf(
                SetupEvent("E-sport", SetupCategory.TEKNIKK)
                    .add(OLE, 1)
                    .add(DOFFEN, 2),
                SetupEvent("Tennis Double", SetupCategory.BALL)
                    .addTeam(1, null, DOLE)
                    .addTeam(2, null, DOFFEN),
                SetupEvent("Padel", SetupCategory.BALL)
                    .add(DOFFEN, 1),
            )
        )

        val allEvents = season.events + seasonOtherRegion1.events + seasonOtherRegion2.events

        val resultsMain = allEvents.calculateSeason(
            seasonId = season.id!!,
            gender = Gender.MALE,
            expectedMangekjemperEvents = season.mangekjemperRequiredEvents.toInt()
        ) { it.events.isMangekjemper(season.mangekjemperRequiredEvents.toInt())}

        val donaldMain = resultsMain.find { it.personName == "Donald Duck" }!!
        donaldMain.shouldHave(name = "Donald Duck", seasonRank = 1, seasonPoints = 4, mangekjemperStatus = true)
        donaldMain.shouldHaveMangekjemperRanks(listOf(1.b, 1.k, 1.b, 1.t), season.events)
        donaldMain.events.find { it.eventName == "Minigolf" }!!.eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        donaldMain.events.find { it.eventName == "Orientering" }!!.eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        donaldMain.events.find { it.eventName == "Bordtennis" }!!.eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        donaldMain.events.find { it.eventName == "Frisbeegolf" }!!.eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        val oleMain = resultsMain.find { it.personName == "Ole" }!!
        oleMain.shouldHave(name = "Ole", seasonRank = 3, seasonPoints = 37, mangekjemperStatus = false)
        oleMain.shouldHaveMangekjemperRanks(listOf(0.b, 0.k, 0.b, 0.t), season.events)
        oleMain.events.find { it.eventName == "Orientering" }!!.eventPointsReason shouldBe PointsReason.NOT_MANGEKJEMPER
        oleMain.events.find { it.eventName == "Frisbeegolf" }!!.eventPointsReason shouldBe PointsReason.NOT_MANGEKJEMPER
        oleMain.events.find { it.eventName == "Roing" }!!.eventPointsReason shouldBe PointsReason.OTHER_REGION_NOT_MANGEKJEMPER
        oleMain.events.find { it.eventName == "E-sport" }!!.eventPointsReason shouldBe PointsReason.OTHER_REGION_NOT_MANGEKJEMPER
        oleMain.seasonPenaltyPoints shouldBe null
        val doleMain = resultsMain.find { it.personName == "Dole" }!!
        doleMain.shouldHave(name = "Dole", seasonRank = 4, seasonPoints = 10, mangekjemperStatus = true)
        doleMain.shouldHaveMangekjemperRanks(listOf(0.b, 1.k, 0.b, 0.t), season.events)
        doleMain.events.find { it.eventName == "Orientering" }!!.eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        doleMain.events.find { it.eventName == "Roing" }!!.eventPointsReason shouldBe PointsReason.OTHER_REGION_MANGEKJEMPER
        doleMain.events.find { it.eventName == "Poker" }!!.eventPointsReason shouldBe PointsReason.OTHER_REGION_MANGEKJEMPER
        doleMain.events.find { it.eventName == "Tennis Double" }!!.eventPointsReason shouldBe PointsReason.OTHER_REGION_MANGEKJEMPER
        val doffenMain = resultsMain.find { it.personName == "Doffen" }!!
        doffenMain.shouldHave(name = "Doffen", seasonRank = 2, seasonPoints = 11, mangekjemperStatus = true)
        doffenMain.events.find { it.eventName == "Orientering" }!!.eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        doffenMain.events.find { it.eventName == "Bordtennis" }!!.eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        doffenMain.events.find { it.eventName == "E-sport" }!!.eventPointsReason shouldBe PointsReason.OTHER_REGION_MANGEKJEMPER
        doffenMain.events.find { it.eventName == "Tennis Double" }!!.eventPointsReason shouldBe PointsReason.OTHER_REGION_MANGEKJEMPER
        doffenMain.events.find { it.eventName == "Padel" }!!.eventPointsReason shouldBe PointsReason.OTHER_REGION_NOT_INCLUDED

        val resultsOther1 = allEvents.calculateSeason(
            seasonId = seasonOtherRegion1.id!!,
            gender = Gender.MALE,
            expectedMangekjemperEvents = seasonOtherRegion1.mangekjemperRequiredEvents.toInt()
        ) {
            it.events.isMangekjemper(season.mangekjemperRequiredEvents.toInt())
        }
        resultsOther1.find { it.personName == "Donald Duck" } shouldBe null
        val oleOther1 = resultsOther1.find { it.personName == "Ole" }!!
        oleOther1.shouldHave(name = "Ole", seasonRank = 2, seasonPoints = 50, mangekjemperStatus = false)
        oleOther1.shouldHaveMangekjemperRanks(listOf(0.k, 0.t), seasonOtherRegion1.events)
        val doleOther1 = resultsOther1.find { it.personName == "Dole" }!!
        doleOther1.shouldHave(name = "Dole", seasonRank = 1, seasonPoints = 4, mangekjemperStatus = true)
        doleOther1.shouldHaveMangekjemperRanks(listOf(1.k, 1.t), seasonOtherRegion1.events)
        resultsOther1.find { it.personName == "Doffen" } shouldBe null

        val resultsOther2 = allEvents.calculateSeason(
            seasonId = seasonOtherRegion2.id!!,
            gender = Gender.MALE,
            expectedMangekjemperEvents = seasonOtherRegion2.mangekjemperRequiredEvents.toInt()
        ) {
            it.events.isMangekjemper(season.mangekjemperRequiredEvents.toInt())
        }
        resultsOther2.find { it.personName == "Donald Duck" } shouldBe null
        val oleOther2 = resultsOther2.find { it.personName == "Ole" }!!
        oleOther2.shouldHave(name = "Ole", seasonRank = 3, seasonPoints = 49, mangekjemperStatus = false)
        oleOther2.shouldHaveMangekjemperRanks(listOf(0.t, 0.b, 0.b), seasonOtherRegion2.events)
        val doleOther2 = resultsOther2.find { it.personName == "Dole" }!!
        doleOther2.shouldHave(name = "Dole", seasonRank = 2, seasonPoints = 7, mangekjemperStatus = true)
        doleOther2.shouldHaveMangekjemperRanks(listOf(0.t, 1.b, 0.b), seasonOtherRegion2.events)
        val doffenOther2 = resultsOther2.find { it.personName == "Doffen" }!!
        doffenOther2.shouldHave(name = "Doffen", seasonRank = 1, seasonPoints = 6, mangekjemperStatus = true)
        doffenOther2.shouldHaveMangekjemperRanks(listOf(1.t, 2.b, 1.b), seasonOtherRegion2.events)
    }

    private fun SeasonParticipant.shouldHave(name: String, seasonRank: Int, seasonPoints: Int, mangekjemperStatus: Boolean) {
        this.personName shouldBe name
        this.seasonRank shouldBe seasonRank
        this.seasonPoints shouldBe seasonPoints
        this.isMangekjemper shouldBe mangekjemperStatus
    }

    private fun SeasonParticipant.shouldHaveMangekjemperRanks(ranks: List<Int>, allEvents: List<Event>) {
        ranks.mapIndexed { index, mangekjemperRank ->
            val event = allEvents[index]
            val simplifiedEvent = this.events.find { it.eventId == event.id }
            "${event.title}-$mangekjemperRank" to "${event.title}-${simplifiedEvent?.mangekjemperRank ?: 0}"
        }.shouldForAll {(expected, actual) ->
            actual shouldBe expected
        }
    }

    // Extension property kun for å gjøre det lettere å lese hva som er ball, teknikk og kondis
    private val Int.b: Int get() = this
    private val Int.k: Int get() = this
    private val Int.t: Int get() = this
}
