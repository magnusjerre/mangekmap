package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Gender

data class SeasonParticipant(
    val personId: Long,
    val personName: String,
    val gender: Gender,
    var seasonRank: Int,
    var seasonPoints: Int,
    val eventParticipations: List<EventParticipation>,
    var seasonPenaltyPoints: SeasonPenaltyPoints? = null,
    var isMangekjemper: Boolean = false
) : Comparable<SeasonParticipant> {
    override fun compareTo(other: SeasonParticipant): Int = seasonRank.compareTo(other.seasonRank)
    override fun toString(): String =
        "${SeasonParticipant::class.qualifiedName}(personId=$personId, personName=\"$personName\")"

    private var rankCount: RankCounts? = null
    fun getRankCounts(): RankCounts {
        if (rankCount == null) {
            rankCount = RankCounts(eventParticipations
                .mapNotNull { it.mangekjemperRank ?: it.actualRank }
                .groupBy { it }
                .map { RankCount(it.key, it.value.size) }
                .toList()
                .sorted())
        }
        return rankCount!!
    }
}

data class EventParticipation(
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

data class RankCount(val rank: Int, val count: Int): Comparable<RankCount> {
    override fun compareTo(other: RankCount): Int {
        if (rank < other.rank) return -1
        if (rank > other.rank) return 1
        return -count.compareTo(other.count)
    }
}