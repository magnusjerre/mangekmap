package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Gender
import kotlin.Comparable
import kotlin.Comparable as Comparable1

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
    val thisSeasonsEventIds = mangekjempere.flatMap { it.events }.filter { ev -> ev.seasonId == seasonId }.map { it.eventId }.distinct()
    val allMangekjempereParticipations = mangekjempere.flatMap { it.events }

    for (eventId in thisSeasonsEventIds) {
        calculateandSetMangekjemperRankingsForEvent(eventId, allMangekjempereParticipations)
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

private fun calculateandSetMangekjemperRankingsForEvent(eventId: Long, eventParticipations: List<SeasonSimplifiedEvent>) {
    val relevantParticipations = eventParticipations.filter { it.eventId == eventId }
    val participationsSortedByActualRank: List<EquallyOrdered<SeasonSimplifiedEvent>> =
        relevantParticipations.sortedWithComparators({ p1, p2 -> p1.actualRank!!.compareTo(p2.actualRank!!) })
    var nextMangekjemperRank = 1

    for (participationsEquallyRanked in participationsSortedByActualRank) {
        participationsEquallyRanked.forEach { it.mangekjemperRank = nextMangekjemperRank }
        nextMangekjemperRank += if (participationsEquallyRanked.first().isTeamBased)
            (participationsEquallyRanked.distinctBy { it.teamNumber }.size)
        else
            participationsEquallyRanked.size()
    }
}

// Returns a list with pairs of Category and utilized SeasonSimplifiedEvents
fun SeasonParticipant.calculateSeasonPoints(
    seasonId: Long,
    penaltyPoints: (Gender) -> Int = { if (it == Gender.MALE) 16 else 8 },
    expectedMangekjemperEvents: Int = 8,
    totalMangekjempere: Int = 1,
): List<Pair<Category?, SeasonSimplifiedEvent>> {
    val physicalConditionEvents = events
        .filter { it.category.name == "Kondisjon" }
        .sortedBy { if (it.seasonId == seasonId) it.mangekjemperRank ?: it.actualRank else totalMangekjempere }
        .toMutableList()

    val ballEvents = events
        .filter { it.category.name == "Balløvelser" }
        .sortedBy { if (it.seasonId == seasonId) it.mangekjemperRank ?: it.actualRank else totalMangekjempere }
        .toMutableList()

    val techniqueEvents = events
        .filter { it.category.name == "Teknikk" }
        .sortedBy { if (it.seasonId == seasonId) it.mangekjemperRank ?: it.actualRank else totalMangekjempere }
        .toMutableList()

    val physicalEventsCalculator = EventPointsCalculator(3, seasonId, totalMangekjempere, penaltyPoints(gender))
    physicalConditionEvents.forEach(physicalEventsCalculator::calculateAndSetEventPoints)

    val ballEventsCalculator = EventPointsCalculator(3, seasonId, totalMangekjempere, penaltyPoints(gender))
    ballEvents.forEach(ballEventsCalculator::calculateAndSetEventPoints)

    val techniqueEventsCalculator = EventPointsCalculator(Int.MAX_VALUE, seasonId, totalMangekjempere, penaltyPoints(gender))
    techniqueEvents.forEach(techniqueEventsCalculator::calculateAndSetEventPoints)

    val chosenEvents = mutableListOf<Pair<Category?, SeasonSimplifiedEvent>>()

    if (techniqueEvents.isNotEmpty()) {
        chosenEvents.add(techniqueEvents.first().category to techniqueEvents.removeFirst())
    }
    if (physicalConditionEvents.isNotEmpty()) {
        chosenEvents.add(physicalConditionEvents.first().category to physicalConditionEvents.removeFirst())
    }
    if (ballEvents.isNotEmpty()) {
        chosenEvents.add(ballEvents.first().category to ballEvents.removeFirst())
    }

    while (chosenEvents.count() < expectedMangekjemperEvents && (physicalConditionEvents.isNotEmpty() || ballEvents.isNotEmpty() || techniqueEvents.isNotEmpty())) {
        val p = physicalConditionEvents.firstOrNull()?.eventPoints ?: Int.MAX_VALUE
        val b = ballEvents.firstOrNull()?.eventPoints ?: Int.MAX_VALUE
        val t = techniqueEvents.firstOrNull()?.eventPoints ?: Int.MAX_VALUE

        if (p < b && p < t) {
            chosenEvents.add(physicalConditionEvents.first().category to physicalConditionEvents.removeFirst())
        } else if (b < t) {
            chosenEvents.add(ballEvents.first().category to ballEvents.removeFirst())
        } else {
            chosenEvents.add(techniqueEvents.first().category to techniqueEvents.removeFirst())
        }
    }

    (physicalConditionEvents + ballEvents + techniqueEvents).forEach {
        it.eventPointsReason = if (it.seasonId == seasonId) PointsReason.NOT_INCLUDED else PointsReason.OTHER_REGION_NOT_INCLUDED
    }

    if (chosenEvents.count() < expectedMangekjemperEvents) {
        this.seasonPenaltyPoints = SeasonPenaltyPoints(expectedMangekjemperEvents, expectedMangekjemperEvents - chosenEvents.count())
    }

    seasonPoints = chosenEvents.filterNot { it.second.eventPointsReason == PointsReason.NOT_INCLUDED }.sumOf { it.second.eventPoints } + (seasonPenaltyPoints?.penaltyPoints ?: 0)
    return chosenEvents
}

fun List<SeasonParticipant>.calculateSeasonRank(seasonId: Long, expectedMangekjemperEvents: Int = 8) {
    val sorted = sortedWithComparators(
        mangekjemperStatusOrEventCountComparator(seasonId, expectedMangekjemperEvents),
        seasonPointsComparator,
        ranksComparator
    )
    var rank = 1
    for (equallyOrderedParticipants in sorted) {
        equallyOrderedParticipants.forEach { participant ->
            participant.seasonRank = rank
        }
        rank += equallyOrderedParticipants.size()
    }
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
    var seasonPenaltyPoints: SeasonPenaltyPoints? = null,
    var isMangekjemper: Boolean = false
) : Comparable1<SeasonParticipant> {
    override fun compareTo(other: SeasonParticipant): Int = seasonRank.compareTo(other.seasonRank)
    override fun toString(): String =
        "${SeasonParticipant::class.qualifiedName}(personId=$personId, personName=\"$personName\")"

    private var rankCount: RankCounts? = null
    fun getRankCounts(): RankCounts {
        if (rankCount == null) {
            rankCount = countRankingsV2()
        }
        return rankCount!!
    }
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

val mangekjemperStatusOrEventCountComparator: (Long, Int) -> Comparator<SeasonParticipant> = { seasonId, expectedMangekjemperRequirement ->
    Comparator { o1, o2 ->
        if (o1 == null && o2 == null) return@Comparator 0
        if (o1 == null) return@Comparator 1
        if (o2 == null) return@Comparator -1
        val o1RelevantEvents = o1.events.filter { it.seasonId == seasonId }
        val o1IsMangekjemperInThisSeasonUsingOnlySeasonevents = o1.isMangekjemper && o1RelevantEvents.isMangekjemper(expectedMangekjemperRequirement)
        val o2RelevantEvents = o2.events.filter { it.seasonId == seasonId }
        val o2IsMangekjemperInThisSeasonUsingOnlySeasonEvents = o2.isMangekjemper && o2RelevantEvents.isMangekjemper(expectedMangekjemperRequirement)
        if (o1IsMangekjemperInThisSeasonUsingOnlySeasonevents &&
                o2IsMangekjemperInThisSeasonUsingOnlySeasonEvents) return@Comparator 0
        if (o1IsMangekjemperInThisSeasonUsingOnlySeasonevents &&
            !o2IsMangekjemperInThisSeasonUsingOnlySeasonEvents) return@Comparator -1
        if (!o1IsMangekjemperInThisSeasonUsingOnlySeasonevents &&
                o2IsMangekjemperInThisSeasonUsingOnlySeasonEvents) return@Comparator 1

        -o1RelevantEvents.count().compareTo(o2RelevantEvents.count())
    }
}

val seasonPointsComparator = Comparator<SeasonParticipant> { o1, o2 ->
    if (o1 == null && o2 == null) return@Comparator 0
    if (o1 == null) return@Comparator 1
    if (o2 == null) return@Comparator -1
    o1.seasonPoints.compareTo(o2.seasonPoints)
}

val ranksComparator = Comparator<SeasonParticipant> { o1, o2 ->
    if (o1 == null && o2 == null) return@Comparator 0
    if (o1 == null) return@Comparator 1
    if (o2 == null) return@Comparator -1
    o1.getRankCounts().compareTo(o2.getRankCounts())
}

fun SeasonParticipant.countRankingsV2(): RankCounts = RankCounts(this.countRankings().map { RankCount(it.first, it.second) })

data class RankCount(val rank: Int, val count: Int): Comparable<RankCount> {
    override fun compareTo(other: RankCount): Int {
        if (rank < other.rank) return -1
        if (rank > other.rank) return 1
        return -count.compareTo(other.count)
    }
}

class RankCounts(rankCounts: Collection<RankCount>) : Comparable<RankCounts> {
    val rankCounts: List<RankCount> = rankCounts.toList().sorted()
    override fun compareTo(other: RankCounts): Int {
        val myRankCounts = if (rankCounts.size < other.rankCounts.size) rankCounts + RankCount(
            rank = other.rankCounts.last().rank + 1,
            count = 0
        ) else rankCounts
        for ((index, rankCount) in myRankCounts.withIndex()) {
            val otherRankCount = other.rankCounts.getOrNull(index) ?: RankCount(rankCount.rank + 1, 0)
            val compareResult = rankCount.compareTo(otherRankCount)
            if (compareResult != 0) return compareResult
        }
        return 0
    }
}