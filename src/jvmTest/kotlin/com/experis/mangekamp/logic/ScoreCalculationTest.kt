package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Gender
import com.experis.mangekamp.models.Participant
import com.experis.mangekamp.models.ParticipantId
import com.experis.mangekamp.models.Person
import com.experis.mangekamp.models.Season
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDate
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
        val seasonPointsUsed = participant.calculateSeasonPoints(1, penaltyPoints, mangekjemperRequirement = simpleMangekjemperRequirement).doSort()
        seasonPointsUsed.count() shouldBe 8
        seasonPointsUsed shouldBe listOf(
            kondisjon to 1,
            kondisjon to 2,
            kondisjon to 4,
            ball to 1,
            ball to 1,
            ball to 6,
            teknikk to 12,
            teknikk to 15
        )
        participant.seasonPoints shouldBe 42
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
                actualRank = if (isMangekjemper) (rank - 1).coerceAtLeast(1) else rank,
                mangekjemperRank = rank,
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
        val seasonPointsUsed = participant.calculateSeasonPoints(1, penaltyPoints, mangekjemperRequirement = simpleMangekjemperRequirement).doSort()
        seasonPointsUsed.shouldBe(
            listOf(
                kondisjon to 1,
                kondisjon to 1,
                kondisjon to 2,
                kondisjon to penaltyPoints(participant.gender),
                kondisjon to 19,
                ball to 8,
                ball to 19,
                teknikk to 5,
            )
        )
        participant.seasonPoints shouldBe 71
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
        val seasonPointsUsed = participant.calculateSeasonPoints(1, penaltyPoints, mangekjemperRequirement = simpleMangekjemperRequirement).doSort()
        seasonPointsUsed.shouldBe(
            listOf(
                kondisjon to 1,
                kondisjon to 2,
                ball to 1,
                ball to 4,
                ball to 6,
                ball to penaltyPoints(participant.gender),
                ball to 18,
                teknikk to 5,
            )
        )
        participant.seasonPoints shouldBe 53
    }

    val penaltyPoints: (Gender) -> Int = { if (it == Gender.MALE) 16 else 8 }

    @Test
    fun `Non-mangekjemper worthy paricipants should receive extra points on their final scores related to the number of missing events`() {
        val participant1Event = listOf(
            kondisjon to 1
        ).toSeasonParticipant(isMangekjemper = false)
        val seasonPointsUsed1 =
            participant1Event.calculateSeasonPoints(1, penaltyPoints, expectedMangekjemperEvents = 8, mangekjemperRequirement = simpleMangekjemperRequirement).doSort()
        seasonPointsUsed1.shouldBe(listOf(kondisjon to 1, null to 56))
        participant1Event.seasonPoints shouldBe 57

        val participant3Event = listOf(
            kondisjon to 1,
            ball to 2,
            teknikk to 4,
        ).toSeasonParticipant(isMangekjemper = false) // Får feil her fordi det gjøres noe logikk rundt mangekjemper-verdien
        val seasonPointsUsed3 =
            participant3Event.calculateSeasonPoints(1, penaltyPoints, expectedMangekjemperEvents = 8, mangekjemperRequirement = simpleMangekjemperRequirement)
        seasonPointsUsed3.shouldBe(
            listOf(
                kondisjon to 1,
                ball to 2,
                teknikk to 4,
                null to 40
            )
        )
        participant3Event.seasonPoints shouldBe 47
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
                    events[4].copy(actualRank = 5),
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
                ),
                isMangekjemper = true
            ),
            SeasonParticipant(
                3,
                "Dole",
                gender = Gender.MALE,
                seasonPoints = 1,
                seasonRank = 0,
                events = events.subList(0, 8),
                isMangekjemper = true
            ),
            SeasonParticipant(
                4,
                "Doffen",
                gender = Gender.MALE,
                seasonPoints = 12,
                seasonRank = 0,
                events = events.subList(0, 8),
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

    @Test
    fun `Should calculate mangekjemper rank for attendance only using the total number of mangekjempere`() {
        val events = seasonEvents.toEvents().subList(0, 5)
        val persons = mutableListOf<Person>()
        events.registerResults(listOf(1.b, 1.k, 1.b, 1.t, 1.t), "Donald Duck", 1010, persons)
        events.registerResults(listOf(2.b, 2.k, 2.b, 0.t, 0.t), "Ole", 1020, persons)
        events.registerResults(listOf(3.b, 3.k, 3.b, 2.t, 0.t), "Onkel Skrue", 1030, persons)
        events.registerResults(listOf(4.b, 4.k, 4.b, 3.t, 2.t), "Dole", 1040, persons)
        events.registerResults(listOf(5.b, 5.k, 5.b, 4.t, 0.t), "Doffen", 1050, persons)
        val season = Season(events.toMutableList(), "season", 2022, 4, 1)
        events.forEach { it.season = season }
        // Make Dole and Doffen attendants only. This way his last event-score should be replaced by 4 which is the number of mangekjempere
        events.last().participants.find { it.id.person.name == "Dole" }!!.isAttendanceOnly = true
        events[3].participants.find { it.id.person.name == "Doffen" }!!.isAttendanceOnly = true

        val result = events.calculateSeason(seasonId = season.id!!, gender = Gender.MALE, expectedMangekjemperEvents = season.mangekjemperRequiredEvents.toInt()) { it.events.isMangekjemper(season.mangekjemperRequiredEvents.toInt()) }
        val winner = result[0]
        winner.shouldHave(name = "Donald Duck", seasonRank = 1, seasonPoints = 4, mangekjemperStatus = true)
        winner.shouldHaveMangekjemperRanks(listOf(1.b, 1.k, 1.b, 1.t, 1.t), events)
        val second = result[1]
        second.shouldHave(name = "Onkel Skrue", seasonRank = 2, seasonPoints = 8, mangekjemperStatus = true)
        second.shouldHaveMangekjemperRanks(listOf(2.b, 2.k, 2.b, 2.t, 0.t), events)
        val third = result[2]
        third.shouldHave(name = "Dole", seasonRank = 3, seasonPoints = 12, mangekjemperStatus = true)
        third.shouldHaveMangekjemperRanks(listOf(3.b, 3.k, 3.b, 3.t, 4.t), events)
        val fourth = result[3]
        fourth.shouldHave(name = "Doffen", seasonRank = 4, 16, mangekjemperStatus = true)
        fourth.shouldHaveMangekjemperRanks(listOf(4.b, 4.k, 4.b, 4.t, 0.t), events)
        val fifth = result[4]
        fifth.shouldHave(name = "Ole", seasonRank = 5, seasonPoints = 10, mangekjemperStatus = false)
        fifth.shouldHaveMangekjemperRanks(listOf(0.b, 0.k, 0.b, 0.t, 0.t), events)
    }

    @Test
    fun `Should correctly calculate season`() {
        val events = seasonEvents.toEvents()
        val persons = mutableListOf<Person>()
        events.registerResults(listOf(1.b, 2.k, 1.b, 2.t, 1.k, 2.t, 1.t, 1.b, 1.b), "Donald Duck", 1010, persons)
        events.registerResults(listOf(4.b, 3.k, 2.b, 3.t, 2.k, 1.t, 2.t, 2.b, 3.b), "Ole", 1020, persons)
        events.registerResults(listOf(2.b, 1.k, 0.b, 0.t, 3.k, 0.t, 0.t, 2.b, 0.b), "Onkel Skrue", 1030, persons)
        events.registerResults(listOf(5.b, 0.k, 4.b, 4.t, 5.k, 4.t, 4.t, 3.b, 4.b), "Dole", 1040, persons)
        events.registerResults(listOf(3.b, 4.k, 3.b, 1.t, 4.k, 3.t, 3.t, 1.b, 2.b), "Doffen", 1050, persons)

        val season = Season(events.toMutableList(), "season", 2022, 8, 1)
        events.forEach {
            it.season = season
        }

        val result = events.calculateSeason(gender = Gender.MALE, expectedMangekjemperEvents = season.mangekjemperRequiredEvents.toInt(), seasonId = season.id!!) { it.events.isMangekjemper()}
        val winner = result[0]
        winner.shouldHave(name = "Donald Duck", seasonRank = 1, seasonPoints = 10, mangekjemperStatus = true)
        winner.shouldHaveMangekjemperRanks(listOf(1.b, 1.k, 1.b, 2.t, 1.k, 2.t, 1.t, 1.b, 1.b), events)
        val second = result[1]
        second.shouldHave(name = "Ole", seasonRank = 2, seasonPoints = 17, mangekjemperStatus = true)
        second.shouldHaveMangekjemperRanks(listOf(3.b, 2.k, 2.b, 3.t, 2.k, 1.t, 2.t, 2.b, 3.b), events)
        val third = result[2]
        third.shouldHave(name = "Doffen", seasonRank = 3, seasonPoints = 18, mangekjemperStatus = true)
        third.shouldHaveMangekjemperRanks(listOf(2.b, 3.k, 3.b, 1.t, 3.k, 3.t, 3.t, 1.b, 2.b), events)
        val fourth = result[3]
        fourth.shouldHave(name = "Dole", seasonRank = 4, seasonPoints = 43, mangekjemperStatus = true)
        fourth.shouldHaveMangekjemperRanks(listOf(4.b, 0.k, 4.b, 4.t, 4.k, 4.t, 4.t, 3.b, 4.b), events)
        val last = result[4]
        last.shouldHave(name = "Onkel Skrue", seasonRank = 5, seasonPoints = 40, mangekjemperStatus = false)
        last.shouldHaveMangekjemperRanks(listOf(0.b, 0.k, 0.b, 0.t, 0.k, 0.t, 0.t, 0.b, 0.b), events)
    }

    @Test
    fun `Should use participation from other regions when assessing mangekjemper status and when calculating their total score`() {
        val eventsMainRegion = seasonEvents.subList(0, 4).toEvents()
        val personsMainRegion = mutableListOf<Person>()
        eventsMainRegion.registerResults(listOf(1.b, 1.k, 1.b, 1.t), "Donald Duck", 1010, personsMainRegion)
        eventsMainRegion.registerResults(listOf(0.b, 3.k, 0.b, 2.t), "Ole", 1020, personsMainRegion)
        eventsMainRegion.registerResults(listOf(0.b, 1.k, 0.b, 0.t), "Dole", 1040, personsMainRegion)
        eventsMainRegion.registerResults(listOf(0.b, 3.k, 2.b, 0.t), "Doffen", 1050, personsMainRegion)

        val seasonMainRegion = Season(eventsMainRegion.toMutableList(), "season", 2022, 4, 1)
        eventsMainRegion.forEach { it.season = seasonMainRegion }

        val eventsOtherRegion1 = seasonEvents.subList(4, 6).toEvents()
        eventsOtherRegion1.registerResults(listOf(2.k, 0.t), "Ole", 1020, personsMainRegion)
        eventsOtherRegion1.registerResults(listOf(1.k, 1.t), "Dole", 1040, personsMainRegion)
        val seasonOtherRegion1 = Season(eventsOtherRegion1.toMutableList(), "season other 1", 2022, 4, 2)
        eventsOtherRegion1.forEach { it.season = seasonOtherRegion1 }

        val eventsOtherRegion2 = seasonEvents.subList(6, 9).toEvents()
        eventsOtherRegion2.registerResults(listOf(1.t, 0.b, 0.b), "Ole", 1020, personsMainRegion)
        eventsOtherRegion2.registerResults(listOf(0.t, 1.b, 0.b), "Dole", 1040, personsMainRegion)
        eventsOtherRegion2.registerResults(listOf(2.t, 2.b, 1.b), "Doffen", 1050, personsMainRegion)
        val seasonOtherRegion2 = Season(eventsOtherRegion2.toMutableList(), "season other 2", 2022, 4, 3)
        eventsOtherRegion2.forEach { it.season = seasonOtherRegion2 }

        val allEvents = eventsMainRegion + eventsOtherRegion1 + eventsOtherRegion2

        // Results from the view of the main region's season
        val resultsMain = allEvents.calculateSeason(
            seasonId = seasonMainRegion.id!!,
            gender = Gender.MALE,
            expectedMangekjemperEvents = seasonMainRegion.mangekjemperRequiredEvents.toInt()
        ) {
            it.events.isMangekjemper(seasonMainRegion.mangekjemperRequiredEvents.toInt())
        }
        val donaldMain = resultsMain.find { it.personName == "Donald Duck" }!!
        donaldMain.shouldHave(name = "Donald Duck", seasonRank = 1, seasonPoints = 4, mangekjemperStatus = true)
        donaldMain.shouldHaveMangekjemperRanks(listOf(1.b, 1.k, 1.b, 1.t), eventsMainRegion)
        val oleMain = resultsMain.find { it.personName == "Ole" }!!
        oleMain.shouldHave(name = "Ole", seasonRank = 4, seasonPoints = 13, mangekjemperStatus = false)
        oleMain.shouldHaveMangekjemperRanks(listOf(0.b, 0.k, 0.b, 0.t), eventsMainRegion)
        val doleMain = resultsMain.find { it.personName == "Dole" }!!
        doleMain.shouldHave(name = "Dole", seasonRank = 2, seasonPoints = 10, mangekjemperStatus = true)
        doleMain.shouldHaveMangekjemperRanks(listOf(0.b, 1.k, 0.b, 0.t), eventsMainRegion)
        val doffenMain = resultsMain.find { it.personName == "Doffen" }!!
        doffenMain.shouldHave(name = "Doffen", seasonRank = 3, seasonPoints = 11, mangekjemperStatus = true)

        val resultsOther1 = allEvents.calculateSeason(
            seasonId = seasonOtherRegion1.id!!,
            gender = Gender.MALE,
            expectedMangekjemperEvents = seasonOtherRegion1.mangekjemperRequiredEvents.toInt()
        ) {
            it.events.isMangekjemper(seasonMainRegion.mangekjemperRequiredEvents.toInt())
        }
        resultsOther1.find { it.personName == "Donald Duck" } shouldBe null
        val oleOther1 = resultsOther1.find { it.personName == "Ole" }!!
        oleOther1.shouldHave(name = "Ole", seasonRank = 2, seasonPoints = 14, mangekjemperStatus = false)
        oleOther1.shouldHaveMangekjemperRanks(listOf(0.k, 0.t), eventsOtherRegion1)
        val doleOther1 = resultsOther1.find { it.personName == "Dole" }!!
        doleOther1.shouldHave(name = "Dole", seasonRank = 1, seasonPoints = 4, mangekjemperStatus = true)
        doleOther1.shouldHaveMangekjemperRanks(listOf(1.k, 1.t), eventsOtherRegion1)
        resultsOther1.find { it.personName == "Doffen" } shouldBe null

        val resultsOther2 = allEvents.calculateSeason(
            seasonId = seasonOtherRegion2.id!!,
            gender = Gender.MALE,
            expectedMangekjemperEvents = seasonOtherRegion2.mangekjemperRequiredEvents.toInt()
        ) {
            it.events.isMangekjemper(seasonMainRegion.mangekjemperRequiredEvents.toInt())
        }
        resultsOther2.find { it.personName == "Donald Duck" } shouldBe null
        val oleOther2 = resultsOther2.find { it.personName == "Ole" }!!
        oleOther2.shouldHave(name = "Ole", seasonRank = 3, seasonPoints = 13, mangekjemperStatus = false)
        oleOther2.shouldHaveMangekjemperRanks(listOf(0.t, 0.b, 0.b), eventsOtherRegion2)
        val doleOther2 = resultsOther2.find { it.personName == "Dole" }!!
        doleOther2.shouldHave(name = "Dole", seasonRank = 2, seasonPoints = 7, mangekjemperStatus = true)
        doleOther2.shouldHaveMangekjemperRanks(listOf(0.t, 1.b, 0.b), eventsOtherRegion2)
        val doffenOther2 = resultsOther2.find { it.personName == "Doffen" }!!
        doffenOther2.shouldHave(name = "Doffen", seasonRank = 1, seasonPoints = 6, mangekjemperStatus = true)
        doffenOther2.shouldHaveMangekjemperRanks(listOf(1.t, 2.b, 1.b), eventsOtherRegion2)
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

    private val seasonEvents = listOf(
        SeasonSimplifiedEvent("Minigolf", ball, 1, 1),
        SeasonSimplifiedEvent("Orientering", kondisjon, 2, 1),
        SeasonSimplifiedEvent("Bordtennis", ball, 3, 1),
        SeasonSimplifiedEvent("Frisbeegolf", teknikk, 4, 1),
        SeasonSimplifiedEvent("Roing", kondisjon, 5, 1),
        SeasonSimplifiedEvent("Poker", teknikk, 6, 1),
        SeasonSimplifiedEvent("E-sport", teknikk, 7, 1),
        SeasonSimplifiedEvent("Tennis Double", ball, 8, 1, isTeamBased = true),
        SeasonSimplifiedEvent("Padel", ball, 9, 1),
    )

    private fun List<SeasonSimplifiedEvent>.toEvents() = map {
        Event(
            date = LocalDate.MIN,
            title = it.eventName,
            category = it.category,
            venue = it.eventName,
            participants = mutableListOf(),
            id = it.eventId,
            isTeamBased = it.isTeamBased
        )
    }

    private fun List<Event>.registerResults(ranks: List<Int>, name: String, id: Long, persons: MutableList<Person>) {
        val person = persons.find { it.id == id } ?: Person(name, "", Gender.MALE, false, id)
        persons += person
        for (i in 0 until ranks.count()) {
            if (ranks[i] != 0) {
                this[i].participants += Participant(
                    rank = ranks[i],
                    isAttendanceOnly = false,
                    score = "",
                    teamNumber = if (this[i].isTeamBased) ranks[i] else null,
                    id = ParticipantId(
                        person,
                        this[i]
                    )
                )
            }
        }
    }
}