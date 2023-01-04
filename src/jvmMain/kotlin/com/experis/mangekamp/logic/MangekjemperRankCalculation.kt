package com.experis.mangekamp.logic

fun List<SeasonParticipant>.calculateMangekjemperRankings(seasonId: Long, mangekjemperRequirement: (SeasonParticipant) -> Boolean) {
    val mangekjempere = this.filter { mangekjemperRequirement(it) && it.eventParticipations.any { ev -> ev.seasonId == seasonId } }
    mangekjempere.forEach { it.isMangekjemper = true }
    val thisSeasonsEventIds = mangekjempere.flatMap { it.eventParticipations }.filter { ev -> ev.seasonId == seasonId }.map { it.eventId }.distinct()
    val allMangekjempereParticipations = mangekjempere.flatMap { it.eventParticipations }

    for (eventId in thisSeasonsEventIds) {
        calculateAndSetMangekjemperRankingsForEvent(eventId, allMangekjempereParticipations)
    }

    for (mangekjemper in mangekjempere) {
        mangekjemper.eventParticipations.filter { ev -> ev.isAttendanceOnly }.forEach {
            it.mangekjemperRank = mangekjempere.count()
        }
    }

    // Set mangekjemper rankings for events not part of this particular seasonId (i.e other regions)
    mangekjempere.flatMap { it.eventParticipations }
        .filterNot { seasonId == it.seasonId }
        .forEach { ev -> ev.mangekjemperRank = mangekjempere.count() }
}

private fun calculateAndSetMangekjemperRankingsForEvent(eventId: Long, eventParticipations: List<EventParticipation>) {
    val relevantParticipations = eventParticipations.filter { it.eventId == eventId }
    val participationsSortedByActualRank: List<EquallyOrdered<EventParticipation>> =
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