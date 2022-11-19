package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Gender
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.exp

fun List<Event>.calculateSeason(
    seasonId: Long,
    gender: Gender,
    penaltyPoints: (Gender) -> Int = { if (it == Gender.MALE) 16 else 8 },
    expectedMangekjemperEvents: Int = 8,
    mangekjemperRequirement: (SeasonParticipant) -> Boolean = { it.events.isMangekjemper(expectedMangekjemperEvents) }
): List<SeasonParticipant> {
    val participants = toSeasonParticipants(gender)
    participants.calculateMangekjemperRankings(seasonId, mangekjemperRequirement)
    val totalMangekjempere = participants.count { it.isMangekjemper }
    participants.forEach {
        it.calculateSeasonPoints(
            seasonId,
            penaltyPoints,
            expectedMangekjemperEvents,
            mangekjemperRequirement,
            totalMangekjempere
        )
    }
    val participantsWithThisAsMainSeason = participants.filter { it.events.any { ev -> ev.seasonId == seasonId } }
    participantsWithThisAsMainSeason.calculateSeasonRank(seasonId, expectedMangekjemperEvents)
    return participantsWithThisAsMainSeason.sorted()
}

private fun List<Event>.toSeasonParticipants(gender: Gender): List<SeasonParticipant> {
    return flatMap { it.participants }
        .filter { it.id.person.gender == gender }
        .groupBy { it.id.person }
        .map { (person, personParticipations) ->
            SeasonParticipant(
                personId = person.id!!.toLong(),
                personName = person.name,
                gender = person.gender,
                seasonRank = -1,
                seasonPoints = -1,
                events = personParticipations.map { pt ->
                    SeasonSimplifiedEvent(
                        eventName = pt.id.event.title,
                        category = pt.id.event.category,
                        actualRank = pt.rank,
                        isAttendanceOnly = pt.isAttendanceOnly,
                        mangekjemperRank = null,
                        eventId = pt.id.event.id!!,
                        seasonId = pt.id.event.season.id!!,
                        isTeamBased = pt.id.event.isTeamBased,
                        teamNumber = pt.teamNumber
                    )
                }
            )
        }
}
fun List<SeasonParticipant>.calculateMangekjemperRankings(seasonId: Long, mangekjemperRequirement: (SeasonParticipant) -> Boolean) {
    val mangekjempere = this.filter { mangekjemperRequirement(it) && it.events.any { ev -> ev.seasonId == seasonId } }
    mangekjempere.forEach { it.isMangekjemper = true }
    val eventIds = mangekjempere.flatMap { it.events }.filter { ev -> ev.seasonId == seasonId }.map { it.eventId }.distinct()
    var counter = 1

    // Calculate mangekjemper rankings for events that are part of this seasonId (i.e region)
    for (eventId in eventIds) {
        val relevantMangekjempere = mangekjempere.filter { it.events.any { e -> e.eventId == eventId } }
            .groupBy {
                val ev = it.events.find { e -> e.eventId == eventId }!!
                ev.teamNumber ?: counter++
            }
            .toList()
            .sortedBy { it.second.first().events.find { e -> e.eventId == eventId }!!.actualRank }

        var teamRank = 1
        var prevTeamEntry = relevantMangekjempere.first()
        prevTeamEntry.second.forEach { it.events.find { e -> e.eventId == eventId }!!.mangekjemperRank = teamRank }
        var prevActualRank = prevTeamEntry.second.first().events.find { e -> e.eventId == eventId}!!.actualRank
        var prevMangekjemperRank = teamRank++
        for (i in 1 until relevantMangekjempere.count()) {
            val currentTeamEntry = relevantMangekjempere[i]
            val relevantResults = currentTeamEntry.second.map { it.events.find { e -> e.eventId == eventId }!! }
            if (prevActualRank == relevantResults.first().actualRank) {
                relevantResults.forEach { it.mangekjemperRank = prevMangekjemperRank }
            } else {
                relevantResults.forEach { it.mangekjemperRank = teamRank }
            }
            teamRank++
            prevTeamEntry = currentTeamEntry
            prevActualRank = relevantResults.first().actualRank
            prevMangekjemperRank = relevantResults.first().mangekjemperRank!!
        }
    }
    for (mangekjemper in mangekjempere) {
        mangekjemper.events.filter { ev -> ev.isAttendanceOnly }.forEach {
            it.mangekjemperRank = mangekjempere.count()
        }
    }

    // Set mangekjemper rankings for events not part of this particular seasonId (i.e other regions)
    mangekjempere.flatMap { it.events }
        .filterNot { seasonId == it.seasonId }
        .forEach { ev -> ev.mangekjemperRank = mangekjempere.count() }
}

// Returns a list with pairs of Category and utilized SeasonSimplifiedEvents
fun SeasonParticipant.calculateSeasonPoints(
    seasonId: Long,
    penaltyPoints: (Gender) -> Int = { if (it == Gender.MALE) 16 else 8 },
    expectedMangekjemperEvents: Int = 8,
    mangekjemperRequirement: (SeasonParticipant) -> Boolean,
    totalMangekjempere: Int = 1,
): List<Pair<Category?, SeasonSimplifiedEvent>> {
    if (mangekjemperRequirement(this)) {
        val physicalConditionCategory = events.first { it.category.name == "Kondisjon" }.category
        val ballCategory = events.first { it.category.name == "Balløvelser" }.category
        val techniqueCategory = events.first { it.category.name == "Teknikk" }.category

        val physicalConditionEventRankings =
            events.asSequence()
                .filter { it.category.name == "Kondisjon" }
                .sortedBy { if (it.seasonId == seasonId) it.mangekjemperRank!! else totalMangekjempere }
                .toMutableList()
        physicalConditionEventRankings.forEachIndexed { index, event ->
            if (index < 3) {
                event.eventPoints = if (event.seasonId == seasonId) event.mangekjemperRank!! else totalMangekjempere
                event.eventPointsReason = if (event.seasonId == seasonId) PointsReason.MANGEKJEMPER else PointsReason.OTHER_REGION_MANGEKJEMPER
            } else {
                event.eventPoints = max(penaltyPoints(this.gender), event.mangekjemperRank ?: event.actualRank!!)
                event.eventPointsReason = PointsReason.MANGEKJEMPER_TOO_MANY_OF_SAME
            }
        }
        val ballEventsRankings = events.asSequence()
            .filter { it.category.name == "Balløvelser" }
            .sortedBy { if (it.seasonId == seasonId) it.mangekjemperRank!! else totalMangekjempere }
            .toMutableList()
        ballEventsRankings.forEachIndexed { index, event ->
            if (index < 3) {
                event.eventPoints = if (event.seasonId == seasonId) event.mangekjemperRank!! else totalMangekjempere
                event.eventPointsReason = if (event.seasonId == seasonId) PointsReason.MANGEKJEMPER else PointsReason.OTHER_REGION_MANGEKJEMPER
            } else {
                event.eventPoints = max(penaltyPoints(this.gender), event.mangekjemperRank ?: event.actualRank!!)
                event.eventPointsReason = PointsReason.MANGEKJEMPER_TOO_MANY_OF_SAME
            }
        }
        val techniqueEventsRankings =
            events.filter { it.category.name == "Teknikk" }
                .sortedBy { if (it.seasonId == seasonId) it.mangekjemperRank!! else totalMangekjempere }
                .toMutableList()
        techniqueEventsRankings.forEachIndexed { index, event ->
            event.eventPoints = if (event.seasonId == seasonId) event.mangekjemperRank!! else totalMangekjempere
            event.eventPointsReason = if (event.seasonId == seasonId) PointsReason.MANGEKJEMPER else PointsReason.OTHER_REGION_MANGEKJEMPER
        }

        val output = mutableListOf<Pair<Category?, SeasonSimplifiedEvent>>()

        output.add(techniqueCategory to techniqueEventsRankings.removeFirst())
        output.add(physicalConditionCategory to physicalConditionEventRankings.removeFirst())
        output.add(ballCategory to ballEventsRankings.removeFirst())

        while (output.count() < expectedMangekjemperEvents && (physicalConditionEventRankings.isNotEmpty() || ballEventsRankings.isNotEmpty() || techniqueEventsRankings.isNotEmpty())) {
            val p = physicalConditionEventRankings.firstOrNull()?.eventPoints ?: Int.MAX_VALUE
            val b = ballEventsRankings.firstOrNull()?.eventPoints ?: Int.MAX_VALUE
            val t = techniqueEventsRankings.firstOrNull()?.eventPoints ?: Int.MAX_VALUE

            if (p < b && p < t) {
                output.add(physicalConditionCategory to physicalConditionEventRankings.removeFirst())
            } else if (b < t) {
                output.add(ballCategory to ballEventsRankings.removeFirst())
            } else {
                output.add(techniqueCategory to techniqueEventsRankings.removeFirst())
            }
        }

        (physicalConditionEventRankings + ballEventsRankings + techniqueEventsRankings).forEach {
            it.eventPointsReason = if (it.seasonId == seasonId) PointsReason.NOT_INCLUDED else PointsReason.OTHER_REGION_NOT_INCLUDED
        }
        seasonPoints = output.filterNot { it.second.eventPointsReason == PointsReason.NOT_INCLUDED }.sumOf { it.second.eventPoints }
        return output
    } else {
        events.forEach {
            it.eventPoints = if (it.seasonId == seasonId) it.actualRank!! else penaltyPoints(gender)
            it.eventPointsReason = if (it.seasonId == seasonId) PointsReason.NOT_MANGEKJEMPER else PointsReason.OTHER_REGION_NOT_MANGEKJEMPER
        }
        val rankingsSortedNew = events.sortedBy { it.eventPoints }

//        val output = rankingsSortedNew.subList(0, min(expectedMangekjemperEvents, rankingsSortedNew.count())).map { it.category to it }
        val output = rankingsSortedNew.map { it.category to it }
        val techniqueEvents = output.filter { it.first.name == "Teknikk" }.toMutableList()
        val ballEvents = output.filter { it.first.name == "Balløvelser" }.toMutableList()
        val physicalConditionEvents = output.filter { it.first.name == "Kondisjon" }.toMutableList()

        val actualOutput = mutableListOf<Pair<Category, SeasonSimplifiedEvent>>()

        if (techniqueEvents.isNotEmpty())
            actualOutput.add(techniqueEvents.removeFirst())
        if (ballEvents.isNotEmpty())
            actualOutput.add(ballEvents.removeFirst())
        if (physicalConditionEvents.isNotEmpty())
            actualOutput.add(physicalConditionEvents.removeFirst())

        while (actualOutput.count() < expectedMangekjemperEvents && (physicalConditionEvents.isNotEmpty() || techniqueEvents.isNotEmpty() || ballEvents.isNotEmpty())) {
            val p = physicalConditionEvents.firstOrNull()?.second?.eventPoints ?: Int.MAX_VALUE
            val b = ballEvents.firstOrNull()?.second?.eventPoints ?: Int.MAX_VALUE
            val t = techniqueEvents.firstOrNull()?.second?.eventPoints ?: Int.MAX_VALUE

            if (p < b && p < t) {
                actualOutput.add(physicalConditionEvents.removeFirst())
            } else if (b < t) {
                actualOutput.add(ballEvents.removeFirst())
            } else {
                actualOutput.add(techniqueEvents.removeFirst())
            }
        }

        if (actualOutput.count() < expectedMangekjemperEvents) {
            this.seasonPenaltyPoints = SeasonPenaltyPoints(expectedMangekjemperEvents, expectedMangekjemperEvents - actualOutput.count())
        }

        (physicalConditionEvents + techniqueEvents + ballEvents).forEach {
            it.second.eventPoints = 0
            it.second.eventPointsReason = PointsReason.NOT_INCLUDED
        }

        seasonPoints = actualOutput.sumOf { it.second.eventPoints } + (seasonPenaltyPoints?.penaltyPoints ?: 0)
        return actualOutput
    }
}

fun List<SeasonParticipant>.calculateSeasonRank(seasonId: Long, expectedMangekjemperEvents: Int = 8) {
    // First pass, will later need to recalculate between those with same ranks
    val sortedSeasonParticipants = sortedBy {
        recalculateSeasonScore(it, seasonId, expectedMangekjemperEvents)
    }
    var rank = 1
    var previousRank = 1
    var previousScore = 0
    for (seasonParticipant in sortedSeasonParticipants) {
        val recalculatedScore = recalculateSeasonScore(seasonParticipant, seasonId, expectedMangekjemperEvents)
        if (recalculatedScore == previousScore) {
            seasonParticipant.seasonRank = previousRank
        } else {
            seasonParticipant.seasonRank = rank
            previousRank = rank
            previousScore = recalculatedScore
        }
        rank++
    }

    // Recalculate internally between tied participants
    val tiedParticipants = sortedSeasonParticipants.groupBy { it.seasonRank }.filter { it.value.count() > 1 }
    for (tiedParticipantsEntry in tiedParticipants) {
        val rankings = tiedParticipantsEntry.value.map {it.personId to it.countRankings() }.toMap()
        val sortable = tiedParticipantsEntry.value.toMutableList()
        sortable.sortWith { a, b ->
            val aRankCount = rankings[a.personId]!!.toMutableList()
            val bRankCount = rankings[b.personId]!!.toMutableList()
            while (aRankCount.isNotEmpty() && bRankCount.isNotEmpty()) {
                val aRank = aRankCount.removeFirst()
                val bRank = bRankCount.removeFirst()
                if (aRank.first < bRank.first) return@sortWith -1
                if (aRank.first > bRank.first) return@sortWith 1
                if (aRank.second != bRank.second) {
                    // Using minus here since we want the one with the most occurrences to come first
                    return@sortWith -aRank.second.compareTo(bRank.second)
                }
            }

            if (aRankCount.isEmpty() && bRankCount.isEmpty()) return@sortWith 0
            else if (aRankCount.isEmpty()) return@sortWith 1
            else return@sortWith -1
        }

        var nextRank = sortable.first().seasonRank + 1
        var previousSeasonParticipant = sortable.first()
        for (seasonParticipant in sortable.subList(1, sortable.size)) {
            if (rankings[seasonParticipant.personId] == rankings[previousSeasonParticipant.personId]) {
                seasonParticipant.seasonRank = previousSeasonParticipant.seasonRank
            } else {
                seasonParticipant.seasonRank = nextRank
            }
            previousSeasonParticipant = seasonParticipant
            nextRank++
        }
    }
}

private fun recalculateSeasonScore(it: SeasonParticipant, seasonId: Long, expectedMangekjemperEvents: Int) =
    if (it.isMangekjemper && it.mainSeasonId() == seasonId) {
        it.seasonPoints
    } else {
        1000 + (expectedMangekjemperEvents - min(
            it.events.count { it.seasonId == seasonId },
            expectedMangekjemperEvents
        )) * 1000 + it.seasonPoints
    }

private fun SeasonParticipant.countRankings(): List<Pair<Int, Int>> =
    events.mapNotNull { it.mangekjemperRank ?: it.actualRank }.groupBy { it }.mapValues { it.value.count() }.toList()
        .sortedBy { it.first }


fun List<SeasonSimplifiedEvent>.isMangekjemper(mangekjemerEventsRequirement: Int = 8, categoryTypes: Int = 3) =
    count() >= mangekjemerEventsRequirement && map { it.category.name }.distinct().count() == categoryTypes

fun <T> List<T>.isMangekjemper(mangekjemperEventsRequirement: Int = 8, categoryTypes: Int = 3, categoryExtractor: (T) -> Any?): Boolean {
    return count() >= mangekjemperEventsRequirement && map { categoryExtractor(it) }.distinct().count() >= categoryTypes
}

data class SeasonParticipant(
    val personId: Long,
    val personName: String,
    val gender: Gender,
    var seasonRank: Int,
    var seasonPoints: Int,
    val events: List<SeasonSimplifiedEvent>,
    var seasonPenaltyPoints: SeasonPenaltyPoints? = null,
    var isMangekjemper: Boolean = false
) : Comparable<SeasonParticipant> {
    override fun compareTo(other: SeasonParticipant): Int = seasonRank.compareTo(other.seasonRank)
    override fun toString(): String =
        "${SeasonParticipant::class.qualifiedName}(personId=$personId, personName=\"$personName\")"

    fun mainSeasonId() = events.groupBy { it.seasonId }.maxByOrNull { it.value.count() }?.key ?: -1
}

data class SeasonSimplifiedEvent(
    val eventName: String,
    val category: Category,
    val eventId: Long,
    val seasonId: Long,
    val actualRank: Int? = null,
    val isAttendanceOnly: Boolean = false,
    var mangekjemperRank: Int? = null,
    var eventPoints: Int = 0,
    var eventPointsReason: PointsReason? = null,
    val teamNumber: Int? = null,
    val isTeamBased: Boolean = false
) {
    override fun toString(): String {
        return "SeasonSimplifiedEvent(eventName=$eventName, category=${category.name}, eventId=$eventId, actualRank=$actualRank, isAttendanceOnly=$isAttendanceOnly, mangekjemperRank=$mangekjemperRank)"
    }
}

data class SeasonPenaltyPoints(
    val pointsPerMissingEvent: Int,
    val numberOfMissingEvents: Int
) {
    val penaltyPoints: Int = pointsPerMissingEvent * numberOfMissingEvents
}

enum class PointsReason {
    NOT_INCLUDED, NOT_MANGEKJEMPER, MANGEKJEMPER, MANGEKJEMPER_TOO_MANY_OF_SAME, OTHER_REGION_NOT_MANGEKJEMPER, OTHER_REGION_MANGEKJEMPER, OTHER_REGION_NOT_INCLUDED
}
