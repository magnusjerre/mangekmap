package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Gender
import com.experis.mangekamp.models.Participant
import com.experis.mangekamp.models.ParticipantId
import com.experis.mangekamp.models.Person
import io.kotest.inspectors.shouldForAll
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
        val minigolf = SeasonSimplifiedEvent("Minigolf", ball, 1, null, null)

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

        participants.calculateMangekjemperRankings { it.isMangekjemper }

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
        val minigolf = SeasonSimplifiedEvent("Minigolf", ball, 1, null, null, isTeamBased = true)

        val participants = listOf(
            minigolf.createParticipant(rank = 2, isMangekjemper = false, id = 1, name = "Donald Duck"),
            minigolf.createParticipant(rank = 2, isMangekjemper = false, id = 2, name = "Ole"),
            minigolf.createParticipant(rank = 3, isMangekjemper = false, id = 3, name = "Dole"),
            minigolf.createParticipant(rank = 3, isMangekjemper = true, id = 4, name = "Doffen"),
            minigolf.createParticipant(rank = 1, isMangekjemper = true, id = 5, name = "Fetter Anton"),
            minigolf.createParticipant(rank = 1, isMangekjemper = true, id = 6, name = "Mikke Mus"),
            minigolf.createParticipant(rank = 1, isMangekjemper = true, id = 7, name = "Langbein"),
            minigolf.createParticipant(rank = 4, isMangekjemper = false, id = 8, name = "Petter Smart"),
            minigolf.createParticipant(rank = 4, isMangekjemper = true, id = 9, name = "Fantonald"),
        )

        participants.calculateMangekjemperRankings { it.isMangekjemper }

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
        gender: Gender = Gender.MALE
    ): SeasonParticipant =
        SeasonParticipant(
            personId = id.toLong(),
            personName = name,
            gender = gender,
            seasonRank = 0,
            seasonPoints = 0,
            events = listOf(this.copy(actualRank = rank))
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
        val seasonPointsUsed = participant.calculateSeasonPoints(penaltyPoints, mangekjemperRequirement = simpleMangekjemperRequirement).doSort()
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
                actualRank = if (isMangekjemper) (rank - 1).coerceAtLeast(1) else rank,
                mangekjemperRank = rank
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
        val seasonPointsUsed = participant.calculateSeasonPoints(penaltyPoints, mangekjemperRequirement = simpleMangekjemperRequirement).doSort()
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
        val seasonPointsUsed = participant.calculateSeasonPoints(penaltyPoints, mangekjemperRequirement = simpleMangekjemperRequirement).doSort()
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
            participant1Event.calculateSeasonPoints(penaltyPoints, expectedMangekjemperEvents = 8, mangekjemperRequirement = simpleMangekjemperRequirement).doSort()
        seasonPointsUsed1.shouldBe(listOf(kondisjon to 1, null to 56))
        participant1Event.seasonPoints shouldBe 57

        val participant3Event = listOf(
            kondisjon to 1,
            ball to 2,
            teknikk to 4,
        ).toSeasonParticipant(isMangekjemper = false) // Får feil her fordi det gjøres noe logikk rundt mangekjemper-verdien
        val seasonPointsUsed3 =
            participant3Event.calculateSeasonPoints(penaltyPoints, expectedMangekjemperEvents = 8, mangekjemperRequirement = simpleMangekjemperRequirement)
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
            SeasonSimplifiedEvent("Minigolf", ball, 1),
            SeasonSimplifiedEvent("Orientering", kondisjon, 2),
            SeasonSimplifiedEvent("Crossfit", kondisjon, 3),
            SeasonSimplifiedEvent("Frisbeegolf", teknikk, 4),
            SeasonSimplifiedEvent("Roing", kondisjon, 5),
            SeasonSimplifiedEvent("Poker", teknikk, 6),
            SeasonSimplifiedEvent("E-sport", teknikk, 7),
            SeasonSimplifiedEvent("Ski med blink", kondisjon, 8),
            SeasonSimplifiedEvent("Padel", ball, 9),
            SeasonSimplifiedEvent("Tverrliggerkonk", ball, 10),
            SeasonSimplifiedEvent("Biptest", kondisjon, 11),
            SeasonSimplifiedEvent("Kontorstolres", teknikk, 12),
            SeasonSimplifiedEvent("Bowling", ball, 13),
            SeasonSimplifiedEvent("Color Line Challenge", teknikk, 14),
            SeasonSimplifiedEvent("Arkadespill", teknikk, 15),
        )
        val participants = listOf(
            SeasonParticipant(
                1,
                "Donald Duck",
                gender = Gender.MALE,
                seasonPoints = 15,
                seasonRank = 0,
                events = emptyList(),
                isMangekjemper = true
            ),
            SeasonParticipant(
                2,
                "Ole",
                gender = Gender.MALE,
                seasonPoints = 20,
                seasonRank = 0,
                events = emptyList(),
                isMangekjemper = true
            ),
            SeasonParticipant(
                3,
                "Dole",
                gender = Gender.MALE,
                seasonPoints = 1,
                seasonRank = 0,
                events = emptyList(),
                isMangekjemper = true
            ),
            SeasonParticipant(
                4,
                "Doffen",
                gender = Gender.MALE,
                seasonPoints = 12,
                seasonRank = 0,
                events = emptyList(),
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
        participants.calculateSeasonRank(8)
        val sorted = participants.sorted()
        sorted[0].personName shouldBe "Dole"
        sorted[0].seasonRank shouldBe 1
        sorted[1].personName shouldBe "Doffen"
        sorted[1].seasonRank shouldBe 2
        sorted[2].personName shouldBe "Donald Duck"
        sorted[2].seasonRank shouldBe 3
        sorted[3].personName shouldBe "Ole"
        sorted[3].seasonRank shouldBe 4
        sorted[4].personName shouldBe "Mikke Mus"
        sorted[4].seasonRank shouldBe 5
        sorted[5].personName shouldBe "Onkel Skrue"
        sorted[5].seasonRank shouldBe 6
        sorted[6].personName shouldBe "Langbein"
        sorted[6].seasonRank shouldBe 7
    }

    @Test
    fun `Should correctly calculate season`() {
        val events = seasonEvents.toEvents()
        events.setupParticipants()

        val result = events.calculateSeason(expectedMangekjemperEvents = 8) { it.events.isMangekjemper()}
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
    private fun SeasonParticipant.shouldHave(name: String, seasonRank: Int, seasonPoints: Int, mangekjemperStatus: Boolean) {
        this.personName shouldBe name
        this.seasonRank shouldBe seasonRank
        this.seasonPoints shouldBe seasonPoints
        this.events.isMangekjemper() shouldBe mangekjemperStatus
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

    private fun List<Event>.setupParticipants() {
        val persons = mutableListOf<Person>()
        registerResults(listOf(1.b, 2.k, 1.b, 2.t, 1.k, 2.t, 1.t, 1.b, 1.b), "Donald Duck", 1010, persons)
        registerResults(listOf(4.b, 3.k, 2.b, 3.t, 2.k, 1.t, 2.t, 2.b, 3.b), "Ole", 1020, persons)
        registerResults(listOf(2.b, 1.k, 0.b, 0.t, 3.k, 0.t, 0.t, 2.b, 0.b), "Onkel Skrue", 1030, persons)
        registerResults(listOf(5.b, 0.k, 4.b, 4.t, 5.k, 4.t, 4.t, 3.b, 4.b), "Dole", 1040, persons)
        registerResults(listOf(3.b, 4.k, 3.b, 1.t, 4.k, 3.t, 3.t, 1.b, 2.b), "Doffen", 1050, persons)
    }

    // Extension property kun for å gjøre det lettere å lese hva som er ball, teknikk og kondis
    private val Int.b: Int get() = this
    private val Int.k: Int get() = this
    private val Int.t: Int get() = this

    private val seasonEvents = listOf(
        SeasonSimplifiedEvent("Minigolf", ball, 1),
        SeasonSimplifiedEvent("Orientering", kondisjon, 2),
        SeasonSimplifiedEvent("Bordtennis", ball, 3),
        SeasonSimplifiedEvent("Frisbeegolf", teknikk, 4),
        SeasonSimplifiedEvent("Roing", kondisjon, 5),
        SeasonSimplifiedEvent("Poker", teknikk, 6),
        SeasonSimplifiedEvent("E-sport", teknikk, 7),
        SeasonSimplifiedEvent("Tennis Double", ball, 8, isTeamBased = true),
        SeasonSimplifiedEvent("Padel", ball, 9),
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

    private fun List<Event>.registerResults(ranks: List<Int>, name: String, id: Long, persons: MutableList<Person>,) {
        val person = Person(name, "", Gender.MALE, false, id)
        persons += person
        for (i in 0 until ranks.count()) {
            if (ranks[i] != 0) {
                this[i].participants += Participant(
                    rank = ranks[i],
                    score = "",
                    id = ParticipantId(
                        person,
                        this[i]
                    )
                )
            }
        }
    }
}