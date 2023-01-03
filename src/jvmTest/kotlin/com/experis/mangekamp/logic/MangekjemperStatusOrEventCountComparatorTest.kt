package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Gender
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class MangekjemperStatusOrEventCountComparatorTest {

    @Test
    fun `Happy case`() {
        val winner = SeasonParticipant(
            personId = 1L,
            personName = "Donald Duck",
            seasonPoints = 3,
            eventParticipations = listOf(
                EventParticipation(MINIGOLF, SetupCategory.BALL.getAsCategory(), 1L, 1L, actualRank = 1, mangekjemperRank = 1),
                EventParticipation(ESPORT, SetupCategory.TEKNIKK.getAsCategory(), 2L, 1L, actualRank = 1, mangekjemperRank = 1),
                EventParticipation(ORIENTERING, SetupCategory.KONDISJON.getAsCategory(), 3L, 1L, actualRank = 1, mangekjemperRank = 1),
                EventParticipation(PADEL, SetupCategory.BALL.getAsCategory(), 4L, 1L, actualRank = 1, mangekjemperRank = 1),
            ),
            gender = Gender.MALE,
            seasonRank = -1,
            isMangekjemper = true
        )
        val second = SeasonParticipant(
            personId = 2L,
            personName = "Onkel Skrue",
            seasonPoints = 6,
            eventParticipations = listOf(
                EventParticipation(MINIGOLF, SetupCategory.BALL.getAsCategory(), 1L, 1L, actualRank = 2, mangekjemperRank = 2),
                EventParticipation(ESPORT, SetupCategory.TEKNIKK.getAsCategory(), 2L, 1L, actualRank = 2, mangekjemperRank = 2),
                EventParticipation(ORIENTERING, SetupCategory.KONDISJON.getAsCategory(), 3L, 1L, actualRank = 2, mangekjemperRank = 2),
            ),
            gender = Gender.MALE,
            seasonRank = -1,
            isMangekjemper = true
        )
        val nonMangekjemper = SeasonParticipant(
            personId = 1L,
            personName = "Donald Duck",
            seasonPoints = 4,
            eventParticipations = listOf(
                EventParticipation(MINIGOLF, SetupCategory.BALL.getAsCategory(), 1L, 1L, actualRank = 3),
                EventParticipation(ESPORT, SetupCategory.TEKNIKK.getAsCategory(), 2L, 1L, actualRank = 3, isAttendanceOnly = true),
            ),
            gender = Gender.MALE,
            seasonRank = -1,
            isMangekjemper = false
        )

        val comparator = mangekjemperStatusOrEventCountComparator(1L, 3)
        comparator.compare(winner, second).shouldBe(0)
        comparator.compare(second, winner).shouldBe(0)
        comparator.compare(winner, nonMangekjemper).shouldBe(-1)
        comparator.compare(nonMangekjemper, winner).shouldBe(1)
        comparator.compare(second, nonMangekjemper).shouldBe(-1)
        comparator.compare(nonMangekjemper, second).shouldBe(1)
    }

    @Test
    fun `Multiple seasons`() {
        val winner = SeasonParticipant(
            personId = 1L,
            personName = "Donald Duck",
            seasonPoints = 3,
            eventParticipations = listOf(
                EventParticipation(MINIGOLF, SetupCategory.BALL.getAsCategory(), 1L, 1L, actualRank = 1, mangekjemperRank = 1),
                EventParticipation(ESPORT, SetupCategory.TEKNIKK.getAsCategory(), 2L, 1L, actualRank = 1, mangekjemperRank = 1),
                EventParticipation(ORIENTERING, SetupCategory.KONDISJON.getAsCategory(), 3L, 1L, actualRank = 1, mangekjemperRank = 1),
                EventParticipation(PADEL, SetupCategory.BALL.getAsCategory(), 4L, 1L, actualRank = 1, mangekjemperRank = 1),
            ),
            gender = Gender.MALE,
            seasonRank = -1,
            isMangekjemper = true
        )
        val secondWithOtherSeasonNonDependent = SeasonParticipant(
            personId = 2L,
            personName = "Onkel Skrue",
            seasonPoints = 6,
            eventParticipations = listOf(
                EventParticipation(MINIGOLF, SetupCategory.BALL.getAsCategory(), 1L, 1L, actualRank = 2, mangekjemperRank = 2),
                EventParticipation(ESPORT, SetupCategory.TEKNIKK.getAsCategory(), 2L, 1L, actualRank = 2, mangekjemperRank = 2),
                EventParticipation(ORIENTERING, SetupCategory.KONDISJON.getAsCategory(), 3L, 1L, actualRank = 2, mangekjemperRank = 2),
                EventParticipation(PADEL, SetupCategory.BALL.getAsCategory(), 1L, 2L, actualRank = 1, mangekjemperRank = 1),
            ),
            gender = Gender.MALE,
            seasonRank = -1,
            isMangekjemper = true
        )
        val thirdPlaceNonMangekjemperOnlyMain = SeasonParticipant(
            personId = 1L,
            personName = "Ole",
            seasonPoints = 4,
            eventParticipations = listOf(
                EventParticipation(MINIGOLF, SetupCategory.BALL.getAsCategory(), 1L, 1L, actualRank = 4),
                EventParticipation(ESPORT, SetupCategory.TEKNIKK.getAsCategory(), 2L, 1L, actualRank = 4, isAttendanceOnly = true),
                EventParticipation(PADEL, SetupCategory.BALL.getAsCategory(), 1L, 2L, actualRank = 2),
            ),
            gender = Gender.MALE,
            seasonRank = -1,
            isMangekjemper = false
        )
        val fourthPlaceOtherSeasonIsMainSeason = SeasonParticipant(
            personId = 2L,
            personName = "Dole",
            seasonPoints = 6,
            eventParticipations = listOf(
                EventParticipation(MINIGOLF, SetupCategory.BALL.getAsCategory(), 1L, 1L, actualRank = 3, mangekjemperRank = 2),
                EventParticipation(ESPORT, SetupCategory.TEKNIKK.getAsCategory(), 2L, 2L, actualRank = 2, mangekjemperRank = 2),
                EventParticipation(ORIENTERING, SetupCategory.KONDISJON.getAsCategory(), 3L, 2L, actualRank = 2, mangekjemperRank = 2),
                EventParticipation(PADEL, SetupCategory.BALL.getAsCategory(), 1L, 2L, actualRank = 3, mangekjemperRank = 2),
            ),
            gender = Gender.MALE,
            seasonRank = -1,
            isMangekjemper = true
        )
        val fifthPlaceOnlyThisSeason = SeasonParticipant(
            personId = 3L,
            personName = "Doffen",
            seasonPoints = 20,
            eventParticipations = listOf(
                EventParticipation(MINIGOLF, SetupCategory.BALL.getAsCategory(), 1L, 1L, actualRank = 5),
            ),
            gender = Gender.MALE,
            seasonRank = -1,
            isMangekjemper = false
        )

        val comparator = mangekjemperStatusOrEventCountComparator(1L, 3)
        val result = listOf(
            fifthPlaceOnlyThisSeason,
            fourthPlaceOtherSeasonIsMainSeason,
            thirdPlaceNonMangekjemperOnlyMain,
            secondWithOtherSeasonNonDependent,
            winner
        ).sortedWith(comparator)
            .map { it.personName }
        result.shouldBe(listOf(
            "Onkel Skrue",  // Doesn't swap with Donald Duck since using this comparator they should be the same
            "Donald Duck",
            "Ole",
            "Doffen", // Doesn't swap with Dole since this comparator they should be the same
            "Dole"
        ))

        comparator.compare(winner, secondWithOtherSeasonNonDependent).shouldBe(0)
        comparator.compare(secondWithOtherSeasonNonDependent, winner).shouldBe(0)
        comparator.compare(secondWithOtherSeasonNonDependent, thirdPlaceNonMangekjemperOnlyMain).shouldBe(-1)
        comparator.compare(thirdPlaceNonMangekjemperOnlyMain, secondWithOtherSeasonNonDependent).shouldBe(1)
        comparator.compare(thirdPlaceNonMangekjemperOnlyMain, fourthPlaceOtherSeasonIsMainSeason).shouldBe(-1)
        comparator.compare(fourthPlaceOtherSeasonIsMainSeason, thirdPlaceNonMangekjemperOnlyMain).shouldBe(1)
        comparator.compare(fourthPlaceOtherSeasonIsMainSeason, fifthPlaceOnlyThisSeason).shouldBe(0)
        comparator.compare(fifthPlaceOnlyThisSeason, fourthPlaceOtherSeasonIsMainSeason).shouldBe(0)
    }
}