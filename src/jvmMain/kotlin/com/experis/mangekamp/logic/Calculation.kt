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
    participants.forEach { it.calculateSeasonPoints(penaltyPoints, expectedMangekjemperEvents, mangekjemperRequirement) }
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
                    isTeamBased = pt.id.event.isTeamBased
                )
            })
    }

fun List<SeasonParticipant>.calculateMangekjemperRankings(mangekjemperRequirement: (SeasonParticipant) -> Boolean) {
    val mangekjempere = this.filter { mangekjemperRequirement(it) }
    mangekjempere.forEach { it.isMangekjemper = true }
    val eventIds = mangekjempere.flatMap { it.events }.map { it.eventId }.distinct()

    for (eventId in eventIds) {
        val relevantMangekjempere = mangekjempere.filter { it.events.any { e -> e.eventId == eventId } }
            .sortedBy { it.events.find { e -> e.eventId == eventId }!!.actualRank }
        var rank = 1
        var prev = relevantMangekjempere.first().events.find { e -> e.eventId == eventId }!!
        prev.mangekjemperRank = rank++
        val isTeamBased = prev.isTeamBased
        for (i in 1 until relevantMangekjempere.count()) {
            val curr = relevantMangekjempere[i].events.find { e -> e.eventId == eventId }!!
            curr.mangekjemperRank = if (curr.actualRank == prev.actualRank) prev.mangekjemperRank else rank
            if (!isTeamBased) {
                rank++
            } else if (curr.actualRank != prev.actualRank) {
                rank++
            }
            prev = curr
        }
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
    sortedBy {
        if (it.isMangekjemper) {
            it.seasonPoints
        } else {
            1000 + (expectedMangekjemperEvents - min(
                it.events.count(),
                expectedMangekjemperEvents
            )) * 1000 + it.seasonPoints
        }
    }.forEachIndexed { index, seasonParticipant -> seasonParticipant.seasonRank = index + 1 }
}

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
    val isTeamBased: Boolean = false
) {
    override fun toString(): String {
        return "SeasonSimplifiedEvent(eventName=$eventName, category=${category.name}, eventId=$eventId, actualRank=$actualRank, mangekjemperRank=$mangekjemperRank)"
    }
}