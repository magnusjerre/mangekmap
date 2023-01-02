package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Gender
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
    val sorted = sortedWithMultiple(
        mangekjemperStatusOrEventCountComparator(seasonId, expectedMangekjemperEvents),
        seasonPointsComparator,
        ranksComparator
    ).makeFlat()
    var rank = 1
    var prevElement: Sorted<SeasonParticipant>? = null
    for (seasonParticipant in sorted) {
        seasonParticipant.obj.seasonRank = if ((prevElement?.order ?: -1) == seasonParticipant.order) prevElement!!.obj.seasonRank else rank
        prevElement = seasonParticipant
        rank++
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

class Sorted<T>(val order: Int, val obj: T)

fun <T> List<T>.sortedWithMultiple(vararg  comparators: Comparator<T>): List<List<Sorted<T>>> {
    val firstSort = this.sortedWith(comparators.first())
    var listlist: MutableList<MutableList<T>> = firstSort.splitInto(comparators.first())

    for (comparator in comparators) {
        if (comparator == comparators.first()) continue
        listlist = listlist.map {
            if (it.count() == 1) {
                mutableListOf( it)
            } else {
                val tempSorted = it.sortedWith(comparator)
                val tempSplit = tempSorted.splitInto(comparator)
                tempSplit
            }
        }.flatten().toMutableList()
    }
    var order = 1
    return listlist.map {
        val oldOrder = order++
        if (it.count() == 1) listOf(Sorted(oldOrder, it.first()))
        else
            it.map { e -> Sorted(oldOrder, e) }

    }
}

fun <T> List<List<Sorted<T>>>.makeFlat(): List<Sorted<T>> = flatten()

fun <T> List<T>.splitInto(comparator: Comparator<T>): MutableList<MutableList<T>> {
    if (this.isEmpty()) return mutableListOf()
    var prev = this.first()
    var listlist = mutableListOf(mutableListOf(prev))
    var fIndex = 1
    while (fIndex < this.count()) {
        val current = this[fIndex]
        if (comparator.compare(prev, current) == 0) {
            listlist.last().add(current)
        } else {
            listlist.add(mutableListOf(current))
        }
        prev = current
        fIndex++
    }
    return listlist
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
data class RankCount(val rank: Int, val count: Int): Comparable1<RankCount> {
    override fun compareTo(other: RankCount): Int {
        if (rank < other.rank) return -1
        if (rank > other.rank) return 1
        return -count.compareTo(other.count)
    }
}
class RankCounts(rankCounts: Collection<RankCount>) : Comparable1<RankCounts> {
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