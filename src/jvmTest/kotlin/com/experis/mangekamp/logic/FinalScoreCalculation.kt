package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Gender
import com.experis.mangekamp.models.Region
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class FinalScoreCalculation {
    @Test
    fun `Should correctly calculate season`() {
        val season = setupSeason(
            id = 1,
            name = "2022-2023",
            region = Region.OSLO,
            mangekjemperRequiredEvents = 8,
            actualPersons = listOf(
                DONALD_DUCK,
                OLE,
                DOLE,
                DOFFEN,
                ONKEL_SKRUE,
            ).toActualPersons(),
            events = listOf(
                minigolf()
                    .add(DONALD_DUCK, 1)
                    .add(ONKEL_SKRUE, 2)
                    .add(DOFFEN, 3)
                    .add(OLE, 4)
                    .add(DOLE, 5),
                orientering()
                    .add(ONKEL_SKRUE, 1)
                    .add(DONALD_DUCK, 2)
                    .add(OLE, 3)
                    .add(DOFFEN, 4),
                bordtennis()
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2)
                    .add(DOFFEN, 3)
                    .add(DOLE, 4),
                frisbeegolf()
                    .add(DOFFEN, 1)
                    .add(DONALD_DUCK, 2)
                    .add(OLE, 3)
                    .add(DOLE, 4),
                roing()
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2)
                    .add(ONKEL_SKRUE, 3)
                    .add(DOFFEN, 4)
                    .add(DOLE, 5),
                poker()
                    .add(OLE, 1)
                    .add(DONALD_DUCK, 2)
                    .add(DOFFEN, 3)
                    .add(DOLE, 4),
                esport()
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2)
                    .add(DOFFEN, 3)
                    .add(DOLE, 4),
                tennisDouble()
                    .addTeam(1, DONALD_DUCK, DOFFEN)
                    .addTeam(2, OLE, ONKEL_SKRUE)
                    .addTeam(3, DOLE),
                padel()
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
        ) { it.eventParticipations.isMangekjemper(season.mangekjemperRequiredEvents.toInt())}

        val winner = result[0]
        winner.shouldHave(person = DONALD_DUCK, seasonRank = 1, seasonPoints = 10, mangekjemperStatus = true)
        winner.shouldHaveMangekjemperRanks(
            MINIGOLF to 1,
            ORIENTERING to 1,
            BORDTENNIS to 1,
            FRISBEEGOLF to 2,
            ROING to 1,
            POKER to 2,
            ESPORT to 1,
            TENNIS_DOUBLE to 1,
            PADEL to 1,
        )
        val second = result[1]
        second.shouldHave(person = OLE, seasonRank = 2, seasonPoints = 17, mangekjemperStatus = true)
        second.shouldHaveMangekjemperRanks(
            MINIGOLF to 3,
            ORIENTERING to 2,
            BORDTENNIS to 2,
            FRISBEEGOLF to 3,
            ROING to 2,
            POKER to 1,
            ESPORT to 2,
            TENNIS_DOUBLE to 2,
            PADEL to 3,
        )
        val third = result[2]
        third.shouldHave(person = DOFFEN, seasonRank = 3, seasonPoints = 18, mangekjemperStatus = true)
        third.shouldHaveMangekjemperRanks(
            MINIGOLF to 2,
            ORIENTERING to 3,
            BORDTENNIS to 3,
            FRISBEEGOLF to 1,
            ROING to 3,
            POKER to 3,
            ESPORT to 3,
            TENNIS_DOUBLE to 1,
            PADEL to 2,
        )
        val fourth = result[3]
        fourth.shouldHave(person = DOLE, seasonRank = 4, seasonPoints = 43, mangekjemperStatus = true)
        fourth.shouldHaveMangekjemperRanks(
            MINIGOLF to 4,
            ORIENTERING to 0,
            BORDTENNIS to 4,
            FRISBEEGOLF to 4,
            ROING to 4,
            POKER to 4,
            ESPORT to 4,
            TENNIS_DOUBLE to 3,
            PADEL to 4,
        )
        val last = result[4]
        last.shouldHave(person = ONKEL_SKRUE, seasonRank = 5, seasonPoints = 40, mangekjemperStatus = false)
        last.shouldHaveMangekjemperRanks(
            MINIGOLF to 0,
            ORIENTERING to 0,
            BORDTENNIS to 0,
            FRISBEEGOLF to 0,
            ROING to 0,
            POKER to 0,
            ESPORT to 0,
            TENNIS_DOUBLE to 0,
            PADEL to 0,
        )
    }

    @Test
    fun `Should calculate mangekjemper rank for attendance only using the total number of mangekjempere`() {
        val season = setupSeason(
            id = 1,
            name = "2022-2023",
            region = Region.OSLO,
            mangekjemperRequiredEvents = 4,
            actualPersons = listOf(
                DONALD_DUCK,
                OLE,
                DOLE,
                DOFFEN,
                ONKEL_SKRUE,
            ).toActualPersons(),
            events = listOf(
                minigolf()
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2)
                    .add(ONKEL_SKRUE, 3)
                    .add(DOLE, 4)
                    .add(DOFFEN, 5),
                orientering()
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2)
                    .add(ONKEL_SKRUE, 3)
                    .add(DOLE, 4)
                    .add(DOFFEN, 5),
                bordtennis()
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2)
                    .add(ONKEL_SKRUE, 3)
                    .add(DOLE, 4)
                    .add(DOFFEN, 5),
                frisbeegolf()
                    .add(DONALD_DUCK, 1)
                    .add(ONKEL_SKRUE, 2)
                    .add(DOLE, 3)
                    .addAttendanceOnly(DOFFEN),
                roing()
                    .add(DONALD_DUCK, 1)
                    .addAttendanceOnly(DOLE)
            )
        )

        val result = season.events.calculateSeason(
            seasonId = season.id!!,
            gender = Gender.MALE,
            expectedMangekjemperEvents = season.mangekjemperRequiredEvents.toInt()
        ) { it.eventParticipations.isMangekjemper(season.mangekjemperRequiredEvents.toInt())}
        val winner = result[0]
        winner.shouldHave(person = DONALD_DUCK, seasonRank = 1, seasonPoints = 4, mangekjemperStatus = true)
        winner.shouldHaveMangekjemperRanks(
            MINIGOLF to 1,
            ORIENTERING to 1,
            BORDTENNIS to 1,
            FRISBEEGOLF to 1,
            ROING to 1,
        )
        val second = result[1]
        second.shouldHave(person = ONKEL_SKRUE, seasonRank = 2, seasonPoints = 8, mangekjemperStatus = true)
        second.shouldHaveMangekjemperRanks(
            MINIGOLF to 2,
            ORIENTERING to 2,
            BORDTENNIS to 2,
            FRISBEEGOLF to 2,
            ROING to 0,
        )
        val third = result[2]
        third.shouldHave(person = DOLE, seasonRank = 3, seasonPoints = 12, mangekjemperStatus = true)
        third.shouldHaveMangekjemperRanks(
            MINIGOLF to 3,
            ORIENTERING to 3,
            BORDTENNIS to 3,
            FRISBEEGOLF to 3,
            ROING to 4,
        )
        val fourth = result[3]
        fourth.shouldHave(person = DOFFEN, seasonRank = 4, 16, mangekjemperStatus = true)
        fourth.shouldHaveMangekjemperRanks(
            MINIGOLF to 4,
            ORIENTERING to 4,
            BORDTENNIS to 4,
            FRISBEEGOLF to 4,
            ROING to 0,
        )
        val fifth = result[4]
        fifth.shouldHave(person = OLE, seasonRank = 5, seasonPoints = 10, mangekjemperStatus = false)
        fifth.shouldHaveMangekjemperRanks(
            MINIGOLF to 0,
            ORIENTERING to 0,
            BORDTENNIS to 0,
            FRISBEEGOLF to 0,
            ROING to 0,
        )
    }

    @Test
    fun `Should use participation from other regions when assessing mangekjemper status and when calculating their total score`() {
        val persons = listOf(
            DONALD_DUCK,
            OLE,
            DOLE,
            DOFFEN
        ).toActualPersons()
        val idGenerator = idGenerator()
        val season = setupSeason(
            id = 1,
            name = "season",
            region = Region.OSLO,
            mangekjemperRequiredEvents = 4,
            actualPersons = persons,
            eventIdGenerator = idGenerator,
            events = listOf(
                minigolf()
                    .add(DONALD_DUCK, 1),
                orientering()
                    .add(DONALD_DUCK, 1)
                    .add(DOLE, 1)
                    .add(OLE, 3)
                    .add(DOFFEN, 3),
                bordtennis()
                    .add(DONALD_DUCK, 1)
                    .add(DOFFEN, 2),
                frisbeegolf()
                    .add(DONALD_DUCK, 1)
                    .add(OLE, 2),
            )
        )

        val seasonOtherRegion1 = setupSeason(
            id = 2,
            name = "season other 1",
            region = Region.TRONDHEIM,
            mangekjemperRequiredEvents = 4,
            actualPersons = persons,
            eventIdGenerator = idGenerator,
            events = listOf(
                roing()
                    .add(DOLE, 1)
                    .add(OLE, 2),
                poker()
                    .add(DOLE, 1),
            )
        )

        val seasonOtherRegion2 = setupSeason(
            id = 3,
            name = "season other 2",
            region = Region.BERGEN,
            mangekjemperRequiredEvents = 4,
            actualPersons = persons,
            eventIdGenerator = idGenerator,
            events = listOf(
                esport()
                    .add(OLE, 1)
                    .add(DOFFEN, 2),
                tennisDouble()
                    .addTeam(1, DOLE)
                    .addTeam(2, DOFFEN),
                padel()
                    .add(DOFFEN, 1),
            )
        )

        val allEvents = season.events + seasonOtherRegion1.events + seasonOtherRegion2.events

        val resultsMain = allEvents.calculateSeason(
            seasonId = season.id!!,
            gender = Gender.MALE,
            expectedMangekjemperEvents = season.mangekjemperRequiredEvents.toInt()
        ) { it.eventParticipations.isMangekjemper(season.mangekjemperRequiredEvents.toInt())}

        val donaldMain = resultsMain.find { it.personName == DONALD_DUCK.name }!!
        donaldMain.shouldHave(person = DONALD_DUCK, seasonRank = 1, seasonPoints = 4, mangekjemperStatus = true)
        donaldMain.shouldHaveMangekjemperRanks(
            MINIGOLF to 1,
            ORIENTERING to 1,
            BORDTENNIS to 1,
            FRISBEEGOLF to 1,
        )
        donaldMain.getEvent(MINIGOLF).eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        donaldMain.getEvent(ORIENTERING).eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        donaldMain.getEvent(BORDTENNIS).eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        donaldMain.getEvent(FRISBEEGOLF).eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        val oleMain = resultsMain.getParticipant(OLE)!!
        oleMain.shouldHave(person = OLE, seasonRank = 3, seasonPoints = 37, mangekjemperStatus = false)
        oleMain.shouldHaveMangekjemperRanks(
            MINIGOLF to 0,
            ORIENTERING to 0,
            BORDTENNIS to 0,
            FRISBEEGOLF to 0,
        )
        oleMain.getEvent(ORIENTERING).eventPointsReason shouldBe PointsReason.NOT_MANGEKJEMPER
        oleMain.getEvent(FRISBEEGOLF).eventPointsReason shouldBe PointsReason.NOT_MANGEKJEMPER
        oleMain.getEvent(ROING).eventPointsReason shouldBe PointsReason.OTHER_REGION_NOT_MANGEKJEMPER
        oleMain.getEvent(ESPORT).eventPointsReason shouldBe PointsReason.OTHER_REGION_NOT_MANGEKJEMPER
        oleMain.seasonPenaltyPoints shouldBe null
        val doleMain = resultsMain.getParticipant(DOLE)!!
        doleMain.shouldHave(person = DOLE, seasonRank = 4, seasonPoints = 10, mangekjemperStatus = true)
        doleMain.shouldHaveMangekjemperRanks(
            MINIGOLF to 0,
            ORIENTERING to 1,
            BORDTENNIS to 0,
            FRISBEEGOLF to 0,
        )
        doleMain.getEvent(ORIENTERING).eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        doleMain.getEvent(ROING).eventPointsReason shouldBe PointsReason.OTHER_REGION_MANGEKJEMPER
        doleMain.getEvent(POKER).eventPointsReason shouldBe PointsReason.OTHER_REGION_MANGEKJEMPER
        doleMain.getEvent(TENNIS_DOUBLE).eventPointsReason shouldBe PointsReason.OTHER_REGION_MANGEKJEMPER
        val doffenMain = resultsMain.getParticipant(DOFFEN)!!
        doffenMain.shouldHave(person = DOFFEN, seasonRank = 2, seasonPoints = 11, mangekjemperStatus = true)
        doffenMain.shouldHaveMangekjemperRanks(
            MINIGOLF to 0,
            ORIENTERING to 3,
            BORDTENNIS to 2,
            FRISBEEGOLF to 0,
        )
        doffenMain.getEvent(ORIENTERING).eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        doffenMain.getEvent(BORDTENNIS).eventPointsReason shouldBe PointsReason.MANGEKJEMPER
        doffenMain.getEvent(ESPORT).eventPointsReason shouldBe PointsReason.OTHER_REGION_MANGEKJEMPER
        doffenMain.getEvent(TENNIS_DOUBLE).eventPointsReason shouldBe PointsReason.OTHER_REGION_MANGEKJEMPER
        doffenMain.getEvent(PADEL).eventPointsReason shouldBe PointsReason.OTHER_REGION_NOT_INCLUDED

        val resultsOther1 = allEvents.calculateSeason(
            seasonId = seasonOtherRegion1.id!!,
            gender = Gender.MALE,
            expectedMangekjemperEvents = seasonOtherRegion1.mangekjemperRequiredEvents.toInt()
        ) {
            it.eventParticipations.isMangekjemper(season.mangekjemperRequiredEvents.toInt())
        }
        resultsOther1.find { it.personName == DONALD_DUCK.name } shouldBe null
        val oleOther1 = resultsOther1.getParticipant(OLE)!!
        oleOther1.shouldHave(person = OLE, seasonRank = 2, seasonPoints = 50, mangekjemperStatus = false)
        oleOther1.shouldHaveMangekjemperRanks(
            ROING to 0,
            POKER to 0
        )
        val doleOther1 = resultsOther1.getParticipant(DOLE)!!
        doleOther1.shouldHave(person = DOLE, seasonRank = 1, seasonPoints = 4, mangekjemperStatus = true)
        doleOther1.shouldHaveMangekjemperRanks(
            ROING to 1,
            POKER to 1
        )
        resultsOther1.getParticipant(DOFFEN) shouldBe null

        val resultsOther2 = allEvents.calculateSeason(
            seasonId = seasonOtherRegion2.id!!,
            gender = Gender.MALE,
            expectedMangekjemperEvents = seasonOtherRegion2.mangekjemperRequiredEvents.toInt()
        ) {
            it.eventParticipations.isMangekjemper(season.mangekjemperRequiredEvents.toInt())
        }
        resultsOther2.getParticipant(DONALD_DUCK) shouldBe null
        val oleOther2 = resultsOther2.getParticipant(OLE)!!
        oleOther2.shouldHave(person = OLE, seasonRank = 3, seasonPoints = 49, mangekjemperStatus = false)
        oleOther2.shouldHaveMangekjemperRanks(
            ESPORT to 0,
            TENNIS_DOUBLE to 0,
            PADEL to 0,
        )
        val doleOther2 = resultsOther2.getParticipant(DOLE)!!
        doleOther2.shouldHave(person = DOLE, seasonRank = 2, seasonPoints = 7, mangekjemperStatus = true)
        doleOther2.shouldHaveMangekjemperRanks(
            ESPORT to 0,
            TENNIS_DOUBLE to 1,
            PADEL to 0
        )
        val doffenOther2 = resultsOther2.getParticipant(DOFFEN)!!
        doffenOther2.shouldHave(person = DOFFEN, seasonRank = 1, seasonPoints = 6, mangekjemperStatus = true)
        doffenOther2.shouldHaveMangekjemperRanks(
            ESPORT to 1,
            TENNIS_DOUBLE to 2,
            PADEL to 1
        )
    }

    private fun SeasonParticipant.shouldHave(person: SetupPerson, seasonRank: Int, seasonPoints: Int, mangekjemperStatus: Boolean) {
        this.personName shouldBe person.name
        this.seasonRank shouldBe seasonRank
        this.seasonPoints shouldBe seasonPoints
        this.isMangekjemper shouldBe mangekjemperStatus
    }

    private fun SeasonParticipant.shouldHaveMangekjemperRanks(vararg ranks: Pair<String, Int>) {
        ranks.map { (eventName, eventRank) ->
            val simplifiedEvent = this.eventParticipations.find { it.eventName == eventName }
            "$eventName-$eventRank" to "$eventName-${simplifiedEvent?.mangekjemperRank ?: 0}"
        }.shouldForAll { (expected, actual) ->
            actual shouldBe expected
        }
    }

    private fun SeasonParticipant.getEvent(eventName: String): EventParticipation = eventParticipations.find { it.eventName == eventName }!!
    private fun List<SeasonParticipant>.getParticipant(person: SetupPerson): SeasonParticipant? = find { it.personName == person.name }
}
