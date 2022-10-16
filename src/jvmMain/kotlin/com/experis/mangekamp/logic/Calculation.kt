package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Gender
import java.lang.Integer.max
import java.lang.Integer.min

fun List<Event>.calculateSeason(
    gender: Gender,
    penaltyPoints: (Gender) -> Int = { if (it == Gender.MALE) 16 else 8 },
    expectedMangekjemperEvents: Int = 8,
    mangekjemperRequirement: (SeasonParticipant) -> Boolean = { it.events.isMangekjemper() }
): List<SeasonParticipant> {
    val participants = toSeasonParticipants(gender)
    participants.calculateMangekjemperRankings(mangekjemperRequirement)
    participants.forEach {
        it.calculateSeasonPoints(
            penaltyPoints,
            expectedMangekjemperEvents,
            mangekjemperRequirement
        )
    }
    participants.calculateSeasonRank(expectedMangekjemperEvents)
    return participants.sorted()
}

private fun List<Event>.toSeasonParticipants(gender: Gender): List<SeasonParticipant> = flatMap { it.participants }
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
                    mangekjemperRank = null,
                    eventId = pt.id.event.id!!,
                    isTeamBased = pt.id.event.isTeamBased,
                    teamNumber = pt.teamNumber
                )
            })
    }

fun List<SeasonParticipant>.calculateMangekjemperRankings(mangekjemperRequirement: (SeasonParticipant) -> Boolean) {
    val mangekjempere = this.filter { mangekjemperRequirement(it) }
    mangekjempere.forEach { it.isMangekjemper = true }
    val eventIds = mangekjempere.flatMap { it.events }.map { it.eventId }.distinct()
    var counter = 1

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


//
//        var rank = 1
//        var prev = relevantMangekjempere.first().second.first().events.find { e -> e.eventId == eventId }!!
//        prev.mangekjemperRank = rank++
//        val isTeamBased = prev.isTeamBased
//        for (i in 1 until relevantMangekjempere.count()) {
//            val curr = relevantMangekjempere[i].events.find { e -> e.eventId == eventId }!!
//            curr.mangekjemperRank = if (curr.actualRank == prev.actualRank) prev.mangekjemperRank else rank
//            if (!isTeamBased) {
//                rank++
//            } else if (curr.actualRank != prev.actualRank) {
//                rank++
//            }
//            prev = curr
//        }
    }
}

// Returns a list with pairs of Category and utilized rank-score
fun SeasonParticipant.calculateSeasonPoints(
    penaltyPoints: (Gender) -> Int = { if (it == Gender.MALE) 16 else 8 },
    expectedMangekjemperEvents: Int = 8,
    mangekjemperRequirement: (SeasonParticipant) -> Boolean
): List<Pair<Category?, Int>> {
    if (mangekjemperRequirement(this)) {
        val physicalConditionCategory = events.first { it.category.name == "Kondisjon" }.category
        val ballCategory = events.first { it.category.name == "Balløvelser" }.category
        val techniqueCategory = events.first { it.category.name == "Teknikk" }.category

        val physicalConditionEventRankings =
            events.asSequence().filter { it.category.name == "Kondisjon" }.map { it.mangekjemperRank!! }.sorted()
                .mapIndexed { index, i ->
                    if (index < 3) i else max(i, penaltyPoints(this.gender))
                }.toMutableList()
        val ballEventsRankings =
            events.asSequence().filter { it.category.name == "Balløvelser" }.map { it.mangekjemperRank!! }.sorted()
                .mapIndexed { index, i ->
                    if (index < 3) i else max(i, penaltyPoints(this.gender))
                }.toMutableList()
        val techniqueEventsRankings =
            events.filter { it.category.name == "Teknikk" }.map { it.mangekjemperRank!! }.sorted().toMutableList()

        val output = mutableListOf<Pair<Category?, Int>>()

        output.add(techniqueCategory to techniqueEventsRankings.removeFirst())
        if (techniqueEventsRankings.isNotEmpty()) {
            output.add(techniqueCategory to techniqueEventsRankings.removeFirst())
        }
        output.add(physicalConditionCategory to physicalConditionEventRankings.removeFirst())
        output.add(ballCategory to ballEventsRankings.removeFirst())

        for (i in output.count() until 8) {
            val p = physicalConditionEventRankings.firstOrNull() ?: Int.MAX_VALUE
            val b = ballEventsRankings.firstOrNull() ?: Int.MAX_VALUE
            val t = techniqueEventsRankings.firstOrNull() ?: Int.MAX_VALUE

            if (p < b && p < t) {
                output.add(physicalConditionCategory to physicalConditionEventRankings.removeFirst())
            } else if (b < t) {
                output.add(ballCategory to ballEventsRankings.removeFirst())
            } else {
                output.add(techniqueCategory to techniqueEventsRankings.removeFirst())
            }
        }

        seasonPoints = output.sumOf { it.second }
        return output
    } else {
        val rankingsSorted = events.map { it.category to it.actualRank!! }.sortedBy { it.second }
        val output = mutableListOf<Pair<Category?, Int>>()
        // Kan teknisk sett ha vært med på mange kondisjon og ball, men ingen teknikk
        output.addAll(rankingsSorted.subList(0, min(expectedMangekjemperEvents, rankingsSorted.count())))
        if (output.count() < expectedMangekjemperEvents) {
            output.add(null to expectedMangekjemperEvents * (expectedMangekjemperEvents - output.count()))
        }
        seasonPoints = output.sumOf { it.second }
        return output
    }
}

fun List<SeasonParticipant>.calculateSeasonRank(expectedMangekjemperEvents: Int = 8) {
    // First pass, will later need to recalculate between those with same ranks
    val sortedSeasonParticipants = sortedBy {
        recalculateSeasonScore(it, expectedMangekjemperEvents)
    }
    var rank = 1
    var previousRank = 1
    var previousScore = 0
    for (seasonParticipant in sortedSeasonParticipants) {
        val recalculatedScore = recalculateSeasonScore(seasonParticipant, expectedMangekjemperEvents)
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
                if (aRank.first == bRank.first && aRank.second != bRank.second) {
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

private fun recalculateSeasonScore(it: SeasonParticipant, expectedMangekjemperEvents: Int) =
    if (it.isMangekjemper) {
        it.seasonPoints
    } else {
        1000 + (expectedMangekjemperEvents - min(
            it.events.count(),
            expectedMangekjemperEvents
        )) * 1000 + it.seasonPoints
    }

private fun SeasonParticipant.countRankings(): List<Pair<Int, Int>> =
    events.mapNotNull { it.mangekjemperRank ?: it.actualRank }.groupBy { it }.mapValues { it.value.count() }.toList()
        .sortedBy { it.first }


fun List<SeasonSimplifiedEvent>.isMangekjemper(mangekjemerEventsRequirement: Int = 8, categoryTypes: Int = 3) =
    count() >= mangekjemerEventsRequirement && map { it.category.name }.distinct().count() == categoryTypes

data class SeasonParticipant(
    val personId: Long,
    val personName: String,
    val gender: Gender,
    var seasonRank: Int,
    var seasonPoints: Int,
    val events: List<SeasonSimplifiedEvent>,
    var isMangekjemper: Boolean = false
) : Comparable<SeasonParticipant> {
    override fun compareTo(other: SeasonParticipant): Int = seasonRank.compareTo(other.seasonRank)
    override fun toString(): String =
        "${SeasonParticipant::class.qualifiedName}(personId=$personId, personName=\"$personName\")"
}

data class SeasonSimplifiedEvent(
    val eventName: String,
    val category: Category,
    val eventId: Long,
    val actualRank: Int? = null,
    var mangekjemperRank: Int? = null,
    val teamNumber: Int? = null,
    val isTeamBased: Boolean = false
) {
    override fun toString(): String {
        return "SeasonSimplifiedEvent(eventName=$eventName, category=${category.name}, eventId=$eventId, actualRank=$actualRank, mangekjemperRank=$mangekjemperRank)"
    }
}