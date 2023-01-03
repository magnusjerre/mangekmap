package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Gender
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class ScoreCalculationTest {
    private val kondisjon = Category("Kondisjon", "red", 1)
    private val ball = Category("Balløvelser", "green", 2)
    private val teknikk = Category("Teknikk", "blue", 3)

    // Mangekjemper-ranking
    @Test
    fun `Mangekjempere should receive a separate mangekjemper-rank recalculated based on the existing event-rank where all non-mangekjempere are omitted from the event`() {
        val minigolf = SeasonSimplifiedEvent("Minigolf", ball, 1, 1, null, false, null)

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

        participants.filterNot { it.isMangekjemper }.shouldForAll { it.events.first().mangekjemperRank shouldBe null }

        val mangekjempere = participants.filter { it.isMangekjemper }
        mangekjempere.shouldForAll { it.events.first().mangekjemperRank shouldNotBe null }
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
        val minigolf = SeasonSimplifiedEvent("Minigolf", ball, 1, 1, null,  false,null, isTeamBased = true)

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

        participants.filterNot { it.isMangekjemper }.shouldForAll { it.events.first().mangekjemperRank shouldBe null }

        val mangekjempere = participants.filter { it.isMangekjemper }
        mangekjempere.shouldForAll { it.events.first().mangekjemperRank shouldNotBe null }
        mangekjempere.count() shouldBe 5
        mangekjempere.getParticipantNamesForMangekjemperRank(1) shouldBe setOf("Fetter Anton", "Mikke Mus", "Langbein")
        mangekjempere.getParticipantNamesForMangekjemperRank(2) shouldBe setOf("Doffen")
        mangekjempere.getParticipantNamesForMangekjemperRank(3) shouldBe setOf("Fantonald")
    }

    private fun List<SeasonParticipant>.getParticipantNamesForMangekjemperRank(rank: Int): Set<String> =
        filter { it.events.first().mangekjemperRank == rank }.map { it.personName }.toSet()

    private fun SeasonSimplifiedEvent.createParticipant(
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
            events = listOf(this.copy(actualRank = rank, teamNumber = teamNumber)),
        ).apply {
            this.isMangekjemper = isMangekjemper
        }

    // Scoring
    @Test
    fun `Mangekjemper participant score should be the sum of the n-best mangekjemper-ranks, using at least one mangekjemper-rank from each category, and at most three from 'physical condition' and 'ball' when possible`() {
        val participant = listOf(
            kondisjon to 1,
            kondisjon to 4,
            kondisjon to 2,
            kondisjon to 8,
            ball to 1,
            ball to 1,
            ball to 6,
            ball to 9,
            teknikk to 22,
            teknikk to 12,
            teknikk to 15,
        ).toSeasonParticipant()
        val seasonPointsUsed = participant.calculateSeasonPoints(1, penaltyPoints).map { it.first to it.second.eventPoints }.doSort()
        seasonPointsUsed.count() shouldBe 8
        seasonPointsUsed shouldBe listOf(
            kondisjon to 1,
            kondisjon to 1,
            kondisjon to 3,
            ball to 1,
            ball to 1,
            ball to 5,
            teknikk to 11,
            teknikk to 14
        )
        participant.seasonPenaltyPoints shouldBe null
        participant.seasonPoints shouldBe 37
    }

    private val simpleMangekjemperRequirement: (SeasonParticipant) -> Boolean = { it.isMangekjemper }

    private fun List<Pair<Category?, Int>>.doSort(): List<Pair<Category?, Int>> = this.sortedBy {
        ((it.first?.id ?: 5) * 100) + it.second
    }

    private fun List<Pair<Category, Int>>.toSeasonParticipant(
        id: Int = 1,
        name: String = "Donald Duck",
        gender: Gender = Gender.MALE,
        isMangekjemper: Boolean = true
    ): SeasonParticipant = SeasonParticipant(
        personId = id.toLong(),
        personName = name,
        gender = gender,
        seasonRank = 0,
        seasonPoints = 0,
        isMangekjemper = isMangekjemper,
        events = this.mapIndexed { index, (category, rank) ->
            SeasonSimplifiedEvent(
                eventName = "${category.name}-$index",
                category = category,
                eventId = (index + 1).toLong(),
                seasonId = 1,
                actualRank = rank,
                mangekjemperRank = if (isMangekjemper) (rank - 1).coerceAtLeast(1) else rank,
            )
        }
    )

    @Test
    fun `Given that a mangekjemper participant has "too few" 'techinque' and 'ball' category events, when calculating the season score, then all superfluous 'physical condition' and 'ball events' should be replaced with a special value`() {
        val participant = listOf(
            kondisjon to 1,
            kondisjon to 2,
            kondisjon to 4,
            kondisjon to 19,
            kondisjon to 1,
            ball to 8,
            ball to 19,
            teknikk to 5,
        ).toSeasonParticipant()
        val seasonPointsUsed =
            participant.calculateSeasonPoints(1, penaltyPoints)
                .map { it.first to it.second.eventPoints }.doSort()
        seasonPointsUsed.shouldBe(
            listOf(
                kondisjon to 1,
                kondisjon to 1,
                kondisjon to 1, // See toSeasonParticipant subtracts 1 from actualRank to mangekjemeprRank
                kondisjon to penaltyPoints(participant.gender),
                kondisjon to 18, // See toSeasonParticipant subtracts 1 from actualRank to mangekjemeprRank
                ball to 7,
                ball to 18,
                teknikk to 4,
            )
        )
        participant.seasonPenaltyPoints shouldBe null
        participant.seasonPoints shouldBe 66
    }

    @Test
    fun `Given that a mangekjemper participant has "too few" 'technique' and 'physical condition' category events, when calculating the season score, then all superfluous 'ball' and 'ball events' should be replaced with a special value`() {
        val participant = listOf(
            kondisjon to 1,
            kondisjon to 2,
            ball to 1,
            ball to 4,
            ball to 6,
            ball to 18,
            ball to 14,
            teknikk to 5,
        ).toSeasonParticipant()
        val seasonPointsUsed = participant.calculateSeasonPoints(1, penaltyPoints).map { it.first to it.second.eventPoints }.doSort()
        seasonPointsUsed.shouldBe(
            listOf(
                kondisjon to 1,
                kondisjon to 1,
                ball to 1,
                ball to 3,
                ball to 5,
                ball to penaltyPoints(participant.gender),
                ball to 17,
                teknikk to 4,
            )
        )
        participant.seasonPoints shouldBe 48
    }

    val penaltyPoints: (Gender) -> Int = { if (it == Gender.MALE) 16 else 8 }

    @Test
    fun `Non-mangekjemper worthy paricipants should receive extra points on their final scores related to the number of missing events`() {
        val participant1Event = listOf(
            kondisjon to 1
        ).toSeasonParticipant(isMangekjemper = false)
        val seasonPointsUsed1 =
            participant1Event.calculateSeasonPoints(1, penaltyPoints, expectedMangekjemperEvents = 8).map { it.first to it.second.eventPoints }.doSort()
        seasonPointsUsed1.shouldBe(listOf(kondisjon to 1))
        participant1Event.seasonPenaltyPoints shouldBe SeasonPenaltyPoints(pointsPerMissingEvent = 8, numberOfMissingEvents = 7)
        participant1Event.seasonPoints shouldBe 57

        val participant3Event = listOf(
            kondisjon to 1,
            ball to 2,
            teknikk to 4,
        ).toSeasonParticipant(isMangekjemper = false) // Får feil her fordi det gjøres noe logikk rundt mangekjemper-verdien
        val seasonPointsUsed3 =
            participant3Event.calculateSeasonPoints(1, penaltyPoints, expectedMangekjemperEvents = 8).map { it.first to it.second.eventPoints }.doSort()
        seasonPointsUsed3.shouldBe(
            listOf(
                kondisjon to 1,
                ball to 2,
                teknikk to 4,
            )
        )
        participant3Event.seasonPenaltyPoints shouldBe SeasonPenaltyPoints(pointsPerMissingEvent = 8, numberOfMissingEvents = 5)
        participant3Event.seasonPoints shouldBe 47
    }

    @Test
    fun `Non-mangekjemper with more events than required to become mangekjemper should prefer events from different categories, and exclude points for "extra" events`() {
        val participant = listOf(
            teknikk to 1,
            teknikk to 2,
            teknikk to 3,
            teknikk to 4,
            teknikk to 5,
            teknikk to 6,
            teknikk to 7,
            teknikk to 8,
            teknikk to 9,
            ball to 10
        ).toSeasonParticipant(isMangekjemper = false)

        participant.calculateSeasonPoints(1, penaltyPoints)

        val excludedEvents = participant.events.filter { it.eventPointsReason == PointsReason.NOT_INCLUDED }
        excludedEvents.count() shouldBe 2
        excludedEvents.map { it.actualRank }.toSet() shouldBe setOf(8, 9)

        val includedEvents = participant.events.filterNot { it.eventPointsReason == PointsReason.NOT_INCLUDED }
        includedEvents.count() shouldBe 8
    }

    @Test
    fun `Should correctly set season rank for both mangekjempere and non-mangekjempere, placing mangekjempere on top, then sorted by the number of completed events and score`() {
        val events = listOf(
            SeasonSimplifiedEvent("Minigolf", ball, 1, 1),
            SeasonSimplifiedEvent("Orientering", kondisjon, 2, 1),
            SeasonSimplifiedEvent("Crossfit", kondisjon, 3, 1),
            SeasonSimplifiedEvent("Frisbeegolf", teknikk, 4, 1),
            SeasonSimplifiedEvent("Roing", kondisjon, 5, 1),
            SeasonSimplifiedEvent("Poker", teknikk, 6, 1),
            SeasonSimplifiedEvent("E-sport", teknikk, 7, 1),
            SeasonSimplifiedEvent("Ski med blink", kondisjon, 8, 1),
            SeasonSimplifiedEvent("Padel", ball, 9, 1),
            SeasonSimplifiedEvent("Tverrliggerkonk", ball, 10, 1),
            SeasonSimplifiedEvent("Biptest", kondisjon, 11, 1),
            SeasonSimplifiedEvent("Kontorstolres", teknikk, 12, 1),
            SeasonSimplifiedEvent("Bowling", ball, 13, 1),
            SeasonSimplifiedEvent("Color Line Challenge", teknikk, 14, 1),
            SeasonSimplifiedEvent("Arkadespill", teknikk, 15, 1),
        )
        val participants = listOf(
            SeasonParticipant(
                1,
                "Donald Duck",
                gender = Gender.MALE,
                seasonPoints = 15,
                seasonRank = 0,
                events = listOf(
                    events[0].copy(actualRank = 1),
                    events[1].copy(actualRank = 1),
                    events[2].copy(actualRank = 2),
                    events[3].copy(actualRank = 2),
                    events[4].copy(actualRank = 7),
                    events[5].copy(actualRank = 3),
                    events[6].copy(actualRank = 7),
                    events[7].copy(actualRank = 7),
                ),
                isMangekjemper = true
            ),
            SeasonParticipant(
                2,
                "Ole",
                gender = Gender.MALE,
                seasonPoints = 15,
                seasonRank = 0,
                events = listOf(
                    events[0].copy(actualRank = 2),
                    events[2].copy(actualRank = 1),
                    events[3].copy(actualRank = 1),
                    events[4].copy(actualRank = 2),
                    events[6].copy(actualRank = 3),
                    events[1].copy(actualRank = 5),
                    events[5].copy(actualRank = 5),
                    events[7].copy(actualRank = 5),
                ),
                isMangekjemper = true
            ),
            SeasonParticipant(
                3,
                "Dole",
                gender = Gender.MALE,
                seasonPoints = 1,
                seasonRank = 0,
                events = listOf(
                    events[0].copy(actualRank = 3),
                    events[1].copy(actualRank = 1),
                    events[2].copy(actualRank = 3),
                    events[3].copy(actualRank = 3),
                    events[4].copy(actualRank = 1),
                    events[5].copy(actualRank = 1),
                    events[6].copy(actualRank = 1),
                    events[7].copy(actualRank = 1),
                ),
                isMangekjemper = true
            ),
            SeasonParticipant(
                4,
                "Doffen",
                gender = Gender.MALE,
                seasonPoints = 12,
                seasonRank = 0,
                events = listOf(
                    events[0].copy(actualRank = 2),
                    events[1].copy(actualRank = 2),
                    events[2].copy(actualRank = 2),
                    events[3].copy(actualRank = 2),
                    events[4].copy(actualRank = 2),
                    events[5].copy(actualRank = 2),
                    events[6].copy(actualRank = 2),
                    events[7].copy(actualRank = 2),
                ),
                isMangekjemper = true
            ),
            SeasonParticipant(
                5,
                "Mikke Mus",
                gender = Gender.MALE,
                seasonPoints = 19,
                seasonRank = 0,
                events = events,
                isMangekjemper = false
            ),
            SeasonParticipant(
                6,
                "Langbein",
                gender = Gender.MALE,
                seasonPoints = 22,
                seasonRank = 0,
                events = events.subList(0, 3),
                isMangekjemper = false
            ),
            SeasonParticipant(
                7,
                "Onkel Skrue",
                gender = Gender.MALE,
                seasonPoints = 19,
                seasonRank = 0,
                events = events.subList(0, 5),
                isMangekjemper = false
            ),
        )
        participants.calculateSeasonRank(seasonId = 1, expectedMangekjemperEvents = 8)
        val sorted = participants.sorted().map { it.personName to it.seasonRank }
        sorted[0] shouldBe ("Dole" to 1)
        sorted[1] shouldBe ("Doffen" to 2)
        sorted[2] shouldBe ("Ole" to 3)
        sorted[3] shouldBe ("Donald Duck" to 4)
        sorted[4] shouldBe ("Mikke Mus" to 5)
        sorted[5] shouldBe ("Onkel Skrue" to 6)
        sorted[6] shouldBe ("Langbein" to 7)
    }

    @Test
    fun `Should correctly set season rank when multiple participants have the same points`() {
        val events = listOf(
            SeasonSimplifiedEvent("Minigolf", ball, 1, 1),
            SeasonSimplifiedEvent("Orientering", kondisjon, 2, 1),
            SeasonSimplifiedEvent("Crossfit", kondisjon, 3, 1),
            SeasonSimplifiedEvent("Frisbeegolf", teknikk, 4, 1),
            SeasonSimplifiedEvent("Roing", kondisjon, 5, 1),
        )
        val participants = listOf(
            SeasonParticipant(
                1,
                "Ole",
                gender = Gender.MALE,
                seasonPoints = 11,
                seasonRank = 0,
                events = listOf(
                    events[0].copy(actualRank = 1),
                    events[1].copy(actualRank = 3),
                    events[2].copy(actualRank = 2),
                    events[3].copy(actualRank = 4),
                    events[4].copy(actualRank = 1),
                ),
                isMangekjemper = true
            ),
            SeasonParticipant(
                2,
                "Dole",
                gender = Gender.MALE,
                seasonPoints = 11,
                seasonRank = 0,
                events = listOf(
                    events[0].copy(actualRank = 2),
                    events[1].copy(actualRank = 1),
                    events[2].copy(actualRank = 3),
                    events[3].copy(actualRank = 2),
                    events[3].copy(actualRank = 3),
                ),
                isMangekjemper = true
            ),
            SeasonParticipant(
                3,
                "Doffen",
                gender = Gender.MALE,
                seasonPoints = 11,
                seasonRank = 0,
                events = listOf(
                    events[0].copy(actualRank = 3),
                    events[1].copy(actualRank = 2),
                    events[2].copy(actualRank = 1),
                    events[3].copy(actualRank = 3),
                    events[3].copy(actualRank = 2),
                ),
                isMangekjemper = true
            ),
            SeasonParticipant(
                4,
                "Donald Duck",
                gender = Gender.MALE,
                seasonPoints = 17,
                seasonRank = 0,
                events = listOf(
                    events[0].copy(actualRank = 4),
                    events[1].copy(actualRank = 4),
                    events[2].copy(actualRank = 4),
                    events[3].copy(actualRank = 1),
                    events[4].copy(actualRank = 4),
                ),
                isMangekjemper = true
            ),
        )
        participants.calculateSeasonRank(8)
        val sorted = participants.sorted().map { it.personName to it.seasonRank }
        sorted shouldContain ("Ole" to 1)
        sorted shouldContain ("Doffen" to 2)
        sorted shouldContain ("Dole" to 2)
        sorted shouldContain ("Donald Duck" to 4)
    }

    @Test
    fun `Should correctly calculate mangekjemper ranking when teams are tied`() {
        val event = SeasonSimplifiedEvent("Tennis double", ball, 1, 1, isTeamBased = true)
        val participants = listOf(
            SeasonParticipant(
                1,
                "Ole",
                gender = Gender.MALE,
                seasonPoints = 11,
                seasonRank = 0,
                events = listOf(event.copy(actualRank = 1, teamNumber = 1)),
                isMangekjemper = true
            ),
            SeasonParticipant(
                2,
                "Dole",
                gender = Gender.MALE,
                seasonPoints = 11,
                seasonRank = 0,
                events = listOf(event.copy(actualRank = 1, teamNumber = 1)),
                isMangekjemper = true
            ),
            SeasonParticipant(
                3,
                "Doffen",
                gender = Gender.MALE,
                seasonPoints = 11,
                seasonRank = 0,
                events = listOf(event.copy(actualRank = 1, teamNumber = 2)),
                isMangekjemper = true
            ),
            SeasonParticipant(
                4,
                "Donald Duck",
                gender = Gender.MALE,
                seasonPoints = 17,
                seasonRank = 0,
                events = listOf(event.copy(actualRank = 1, teamNumber = 2)),
                isMangekjemper = true
            ),
            SeasonParticipant(
                4,
                "Onkel Skrue",
                gender = Gender.MALE,
                seasonPoints = 17,
                seasonRank = 0,
                events = listOf(event.copy(actualRank = 2, teamNumber = 3)),
                isMangekjemper = true
            ),
        )
        participants.calculateMangekjemperRankings(1, simpleMangekjemperRequirement)
        val mangekjemperRanks = participants.map {
            it.personName to it.events.first().mangekjemperRank
        }
        mangekjemperRanks shouldContain ("Ole" to 1)
        mangekjemperRanks shouldContain ("Dole" to 1)
        mangekjemperRanks shouldContain ("Doffen" to 1)
        mangekjemperRanks shouldContain ("Donald Duck" to 1)
        mangekjemperRanks shouldContain ("Onkel Skrue" to 3)
    }

}