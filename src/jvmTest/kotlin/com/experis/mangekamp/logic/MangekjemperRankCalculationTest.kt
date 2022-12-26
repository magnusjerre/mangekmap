package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Gender
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class MangekjemperRankCalculationTest {
    private val kondisjon = Category("Kondisjon", "red", 1)
    private val ball = Category("Balløvelser", "green", 2)
    private val teknikk = Category("Teknikk", "blue", 3)

    @Test
    fun `Mangekjempere should receive a separate mangekjemper-rank recalculated based on the existing event-rank where all non-mangekjempere are omitted from the event`() {
        val minigolf = EventParticipation("Minigolf", ball, 1, 1, null, false, null)

        val participants = listOf(
            minigolf.createParticipant(rank = 2, isMangekjemper = true, id = 1, name = "Donald Duck"),
            minigolf.createParticipant(rank = 5, isMangekjemper = true, id = 2, name = "Ole"),
            minigolf.createParticipant(rank = 5, isMangekjemper = false, id = 3, name = "Dole"),
            minigolf.createParticipant(rank = 5, isMangekjemper = true, id = 4, name = "Doffen"),
            minigolf.createParticipant(rank = 1, isMangekjemper = false, id = 5, name = "Fetter Anton"),
            minigolf.createParticipant(rank = 3, isMangekjemper = true, id = 6, name = "Mikke Mus"),
            minigolf.createParticipant(rank = 3, isMangekjemper = true, id = 7, name = "Langbein"),
            minigolf.createParticipant(rank = 8, isMangekjemper = false, id = 8, name = "Petter Smart"),
            minigolf.createParticipant(rank = 9, isMangekjemper = true, id = 9, name = "Fantonald"),
            minigolf.createParticipant(rank = 9, isMangekjemper = true, id = 10, name = "Onkel Skrue"),
        )

        participants.calculateMangekjemperRankings(1) { it.isMangekjemper }

        participants.filterNot { it.isMangekjemper }.shouldForAll { it.eventParticipations.first().mangekjemperRank shouldBe null }

        val mangekjempere = participants.filter { it.isMangekjemper }
        mangekjempere.shouldForAll { it.eventParticipations.first().mangekjemperRank shouldNotBe null }
        mangekjempere.count() shouldBe 7
        mangekjempere.getParticipantNamesForMangekjemperRank(1) shouldBe setOf("Donald Duck")
        mangekjempere.getParticipantNamesForMangekjemperRank(2) shouldBe setOf("Mikke Mus", "Langbein")
        mangekjempere.getParticipantNamesForMangekjemperRank(3) shouldBe emptySet()
        mangekjempere.getParticipantNamesForMangekjemperRank(4) shouldBe setOf("Ole", "Doffen")
        mangekjempere.getParticipantNamesForMangekjemperRank(5) shouldBe emptySet()
        mangekjempere.getParticipantNamesForMangekjemperRank(6) shouldBe setOf("Fantonald", "Onkel Skrue")
    }

    @Test
    fun `Mangekjempere team ranking should receive a separate mangekjemper-rank recalculated based on the existing event-rank where all non-mangekjempere are omitted from the event`() {
        val minigolf = EventParticipation("Minigolf", ball, 1, 1, null,  false,null, isTeamBased = true)

        val participants = listOf(
            minigolf.createParticipant(rank = 2, teamNumber = 2, isMangekjemper = false, id = 1, name = "Donald Duck"),
            minigolf.createParticipant(rank = 2, teamNumber = 2, isMangekjemper = false, id = 2, name = "Ole"),
            minigolf.createParticipant(rank = 3, teamNumber = 3, isMangekjemper = false, id = 3, name = "Dole"),
            minigolf.createParticipant(rank = 3, teamNumber = 3, isMangekjemper = true, id = 4, name = "Doffen"),
            minigolf.createParticipant(rank = 1, teamNumber = 1, isMangekjemper = true, id = 5, name = "Fetter Anton"),
            minigolf.createParticipant(rank = 1, teamNumber = 1, isMangekjemper = true, id = 6, name = "Mikke Mus"),
            minigolf.createParticipant(rank = 1, teamNumber = 1, isMangekjemper = true, id = 7, name = "Langbein"),
            minigolf.createParticipant(rank = 4, teamNumber = 4, isMangekjemper = false, id = 8, name = "Petter Smart"),
            minigolf.createParticipant(rank = 4, teamNumber = 4, isMangekjemper = true, id = 9, name = "Fantonald"),
        )

        participants.calculateMangekjemperRankings(1) { it.isMangekjemper }

        participants.filterNot { it.isMangekjemper }.shouldForAll { it.eventParticipations.first().mangekjemperRank shouldBe null }

        val mangekjempere = participants.filter { it.isMangekjemper }
        mangekjempere.shouldForAll { it.eventParticipations.first().mangekjemperRank shouldNotBe null }
        mangekjempere.count() shouldBe 5
        mangekjempere.getParticipantNamesForMangekjemperRank(1) shouldBe setOf("Fetter Anton", "Mikke Mus", "Langbein")
        mangekjempere.getParticipantNamesForMangekjemperRank(2) shouldBe setOf("Doffen")
        mangekjempere.getParticipantNamesForMangekjemperRank(3) shouldBe setOf("Fantonald")
    }

    @Test
    fun `Should correctly calculate mangekjemper ranking when teams are tied`() {
        val event = EventParticipation("Tennis double", ball, 1, 1, isTeamBased = true)
        val participants = listOf(
            SeasonParticipant(
                1,
                "Ole",
                gender = Gender.MALE,
                seasonPoints = 11,
                seasonRank = 0,
                eventParticipations = listOf(event.copy(actualRank = 1, teamNumber = 1)),
                isMangekjemper = true
            ),
            SeasonParticipant(
                2,
                "Dole",
                gender = Gender.MALE,
                seasonPoints = 11,
                seasonRank = 0,
                eventParticipations = listOf(event.copy(actualRank = 1, teamNumber = 1)),
                isMangekjemper = true
            ),
            SeasonParticipant(
                3,
                "Doffen",
                gender = Gender.MALE,
                seasonPoints = 11,
                seasonRank = 0,
                eventParticipations = listOf(event.copy(actualRank = 1, teamNumber = 2)),
                isMangekjemper = true
            ),
            SeasonParticipant(
                4,
                "Donald Duck",
                gender = Gender.MALE,
                seasonPoints = 17,
                seasonRank = 0,
                eventParticipations = listOf(event.copy(actualRank = 1, teamNumber = 2)),
                isMangekjemper = true
            ),
            SeasonParticipant(
                4,
                "Onkel Skrue",
                gender = Gender.MALE,
                seasonPoints = 17,
                seasonRank = 0,
                eventParticipations = listOf(event.copy(actualRank = 2, teamNumber = 3)),
                isMangekjemper = true
            ),
        )
        participants.calculateMangekjemperRankings(1) { it.isMangekjemper }
        val mangekjemperRanks = participants.map {
            it.personName to it.eventParticipations.first().mangekjemperRank
        }
        mangekjemperRanks shouldContain ("Ole" to 1)
        mangekjemperRanks shouldContain ("Dole" to 1)
        mangekjemperRanks shouldContain ("Doffen" to 1)
        mangekjemperRanks shouldContain ("Donald Duck" to 1)
        mangekjemperRanks shouldContain ("Onkel Skrue" to 3)
    }

    private fun EventParticipation.createParticipant(
        id: Int,
        name: String,
        rank: Int,
        isMangekjemper: Boolean,
        teamNumber: Int? = null,
        gender: Gender = Gender.MALE
    ): SeasonParticipant =
        SeasonParticipant(
            personId = id.toLong(),
            personName = name,
            gender = gender,
            seasonRank = 0,
            seasonPoints = 0,
            eventParticipations = listOf(this.copy(actualRank = rank, teamNumber = teamNumber)),
        ).apply {
            this.isMangekjemper = isMangekjemper
        }

    private fun List<SeasonParticipant>.getParticipantNamesForMangekjemperRank(rank: Int): Set<String> =
        filter { it.eventParticipations.first().mangekjemperRank == rank }.map { it.personName }.toSet()
}