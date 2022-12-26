package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Gender
import com.experis.mangekamp.models.Participant
import com.experis.mangekamp.models.ParticipantId
import com.experis.mangekamp.models.Person
import com.experis.mangekamp.models.Region
import com.experis.mangekamp.models.Season
import java.lang.NullPointerException
import java.time.LocalDate

enum class SetupCategory(val prettyName: String) {
    TEKNIKK("Teknikk"), KONDISJON("Kondisjon"), BALL("Balløvelser");

    internal fun getAsCategory(): Category = Category(name = this.prettyName, color = "123", id = this.ordinal.toLong())
}

data class SetupEvent(val name: String, val category: SetupCategory) {
    val participants: MutableList<SetupParticipant> = mutableListOf()
    val participations: MutableList<SetupParticipation> = mutableListOf()
    fun add(participant: SetupParticipant): SetupEvent {
        participants.add(participant)
        participations.add(SetupParticipation(participant))
        return this
    }

    fun addAttendanceOnly(name: String): SetupEvent = apply {
        participants.add(SetupParticipant(name, null, -1, null, true))
        participations.add(SetupParticipation(SetupParticipant(name, null, -1, null, true)))
    }

    fun addTeam(rank: Int, score: String?, vararg names: String): SetupEvent = apply {
        val teamNumber = (participants.lastOrNull()?.teamNumber ?: 0) + 1
        names.forEach { participants.add(SetupParticipant(it, null, rank, score, false, teamNumber)) }
    }

    fun addTeam(rank: Int, score: String?, vararg setupPersons: SetupPerson): SetupEvent = apply {
        val teamNumber = (participants.lastOrNull()?.teamNumber ?: 0) + 1
        setupPersons.forEach { participants.add(SetupParticipant(it.name, it.gender, rank, score, false, teamNumber)) }
    }

    fun add(name: String, rank: Int, score: String? = null): SetupEvent {
        participants.add(SetupParticipant(name, null, rank, score))
        participations.add(SetupParticipation(SetupParticipant(name, null, rank, score)))
        return this
    }

    fun add(person: SetupPerson, rank: Int, score: String? = null): SetupEvent = apply {
        participants.add(SetupParticipant(person.name, person.gender, rank, score))
    }

    fun addAttendanceOnly(person: SetupPerson): SetupEvent = apply {
        participants.add(SetupParticipant(person.name, person.gender, -1, null, true))
    }
}

class SetupParticipation(val participants: MutableList<SetupParticipant> = mutableListOf()) {
    constructor(vararg participants: SetupParticipant) : this() {
        this.participants.addAll(participants)
    }
}

data class SetupParticipant(
    val name: String,
    val gender: Gender?,
    val rank: Int,
    val score: String? = null,
    val attendanceOnly: Boolean = false,
    val teamNumber: Int? = null
)

data class SetupPerson(val name: String, val gender: Gender, val id: Long)

val events = listOf(
    SetupEvent("Minigolf", SetupCategory.BALL)
        .add("Donald Duck", 1, "jdjd")
        .add("Dolly Duck", 1, null)
        .addAttendanceOnly("Onkel Skrue")
        .addTeam(2, score = "jfjf", "Winnie", "The", "Pooh")
)

val persons = listOf(
    SetupPerson("Donald Duck", Gender.MALE, 1),
    SetupPerson("Dolly Duck", Gender.FEMALE, 2)
)

val SEASON = setupSeason(
    "2022-20223", Region.OSLO, 4,
    listOf(
        SetupPerson("Donald Duck", Gender.MALE, 1),
        SetupPerson("Onkel Skrue", Gender.MALE, 2),
        SetupPerson("Langbein", Gender.MALE, 3),
        SetupPerson("Dolly Duck", Gender.FEMALE, 4),
    ),
    listOf(
        SetupEvent("Minigolf", SetupCategory.BALL)
            .add("Donald Duck", 1, "56")
            .add("Onkel Skrue", 2, "60")
            .add("Dolly Duck", 1),
        SetupEvent("Volleyball", SetupCategory.BALL)
            .addTeam(1, "3 seiere", "Donald Duck", "Langbein")
            .addTeam(2, "3 tap", "Onkel Skrue", "Dolly Duck"),
        SetupEvent("Orientering", SetupCategory.KONDISJON)
            .add("Donald Duck", 1, "3 min")
            .add("Langbein", 2, "6 min")
            .addAttendanceOnly("Onkel Skrue"),
    )
)

fun List<SetupPerson>.toActualPersons(): List<Person> = map {
    Person(name = it.name, email = it.name, gender = it.gender, retired = false, id = it.id).apply {
        participants = mutableListOf()
    }
}

fun setupSeason(name: String, region: Region, mangekjemperRequiredEvents: Short, persons: List<SetupPerson>, events: List<SetupEvent>): Season {
    val actualPersons =
        persons.map { Person(name = it.name, email = it.name, gender = it.gender, retired = false, id = it.id) }

    fun getPerson(sp: SetupParticipant): Person = actualPersons.find { it.name == sp.name }
        ?: throw NullPointerException("""Found no actual person for name "${sp.name}"""")

    var date = LocalDate.of(2022, 8, 23)

    val season = Season(
        name = name,
        startYear = 2022,
        region = region,
        mangekjemperRequiredEvents = mangekjemperRequiredEvents,
        id = 1,
        events = mutableListOf())

    events.forEachIndexed { index, ev ->
        val isTeamBased = ev.participants.filterNot { it.teamNumber == null }.groupBy { it.teamNumber }.any { it.component2().count() > 1 }
        val participants = mutableListOf<Participant>()
        val event = Event(
            date = date,
            title = ev.name,
            category = ev.category.getAsCategory(),
            venue = "",
            participants = participants,
            isTeamBased = isTeamBased,
            id = (index + 1).toLong()
        )
        event.season = season
        ev.participants.forEach { evParticipant ->
            val person = getPerson(evParticipant)
            val rank = if (evParticipant.attendanceOnly) ev.participants.count() else evParticipant.rank
            val participant = Participant(
                rank = rank,
                isAttendanceOnly = evParticipant.attendanceOnly,
                score = evParticipant.score ?: "",
                teamNumber = evParticipant.teamNumber,
                id = ParticipantId(person = person, event = event)
            )
            participants.add(participant)
        }
        season.events.add(event)
        date = date.plusDays(20)
    }

    return season
}

fun setupSeasonV2(id: Long, name: String, region: Region, mangekjemperRequiredEvents: Short, eventIdGenerator: () -> Long, actualPersons: List<Person>, events: List<SetupEvent>): Season {
    fun getPerson(sp: SetupParticipant): Person = actualPersons.find { it.name == sp.name }
        ?: throw NullPointerException("""Found no actual person for name "${sp.name}"""")

    var date = LocalDate.of(2022, 8, 23)

    val season = Season(
        name = name,
        startYear = 2022,
        region = region,
        mangekjemperRequiredEvents = mangekjemperRequiredEvents,
        id = id,
        events = mutableListOf())

    events.forEachIndexed { index, ev ->
        val isTeamBased = ev.participants.any { it.teamNumber != null } //filterNot { it.teamNumber == null }.groupBy { it.teamNumber }.any { it.component2().count() > 1 }
        val participants = mutableListOf<Participant>()
        val event = Event(
            date = date,
            title = ev.name,
            category = ev.category.getAsCategory(),
            venue = "",
            participants = participants,
            isTeamBased = isTeamBased,
            id = eventIdGenerator()
        )
        event.season = season
        ev.participants.forEach { evParticipant ->
            val person = getPerson(evParticipant)
            val rank = if (evParticipant.attendanceOnly) ev.participants.count() else evParticipant.rank
            val participant = Participant(
                rank = rank,
                isAttendanceOnly = evParticipant.attendanceOnly,
                score = evParticipant.score ?: "",
                teamNumber = evParticipant.teamNumber,
                id = ParticipantId(person = person, event = event)
            )
            participants.add(participant)
            (person.participants as MutableList).add(participant)
        }
        season.events.add(event)
        date = date.plusDays(20)
    }

    return season
}

val DONALD_DUCK = SetupPerson("Donald Duck", Gender.MALE, 1)
val OLE = SetupPerson("Ole", Gender.MALE, 3)
val DOLE = SetupPerson("Dole", Gender.MALE, 2)
val DOFFEN = SetupPerson("Doffen", Gender.MALE, 4)
val ONKEL_SKRUE = SetupPerson("Onkel Skrue", Gender.MALE, 5)
