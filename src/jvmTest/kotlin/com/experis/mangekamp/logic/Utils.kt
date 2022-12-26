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

    fun addTeam(rank: Int, vararg setupPersons: SetupPerson): SetupEvent = apply {
        val teamNumber = (participants.lastOrNull()?.teamNumber ?: 0) + 1
        setupPersons.forEach { participants.add(SetupParticipant(it.name, it.gender, rank, null, false, teamNumber)) }
    }

    fun add(person: SetupPerson, rank: Int, score: String? = null): SetupEvent = apply {
        participants.add(SetupParticipant(person.name, person.gender, rank, score))
    }

    fun addAttendanceOnly(person: SetupPerson): SetupEvent = apply {
        participants.add(SetupParticipant(person.name, person.gender, -1, null, true))
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

val MINIGOLF = "Minigolf"
val ORIENTERING = "Orientering"
val BORDTENNIS = "Bordtennis"
val FRISBEEGOLF = "Frisbeegolf"
val ROING = "Roing"
val POKER = "Poker"
val ESPORT = "E-sport"
val TENNIS_DOUBLE = "Tennis Double"
val PADEL = "Padel"
fun minigolf() = SetupEvent(MINIGOLF, SetupCategory.BALL)
fun orientering() = SetupEvent(ORIENTERING, SetupCategory.KONDISJON)
fun bordtennis() = SetupEvent(BORDTENNIS, SetupCategory.BALL)
fun frisbeegolf() = SetupEvent(FRISBEEGOLF, SetupCategory.TEKNIKK)
fun roing() = SetupEvent(ROING, SetupCategory.KONDISJON)
fun poker() = SetupEvent(POKER, SetupCategory.TEKNIKK)
fun esport() = SetupEvent(ESPORT, SetupCategory.TEKNIKK)
fun tennisDouble() = SetupEvent(TENNIS_DOUBLE, SetupCategory.BALL)
fun padel() = SetupEvent(PADEL, SetupCategory.BALL)