package com.experis.mangekamp.controllers.csvimport

import com.experis.mangekamp.exceptions.UnknownInternalServerError
import com.experis.mangekamp.models.Gender
import com.experis.mangekamp.models.Person
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.springframework.web.client.HttpServerErrorException.InternalServerError


fun List<String>.mapPersons(): List<Person> {
    val headers = first().toPersonHeadersIndex()
    return subList(1, size).map { it.toPerson(headers) }
}

private fun String.toPersonHeadersIndex(): Map<String, Int> = split(";")
    .mapIndexed { index, header -> header to index }
    .toMap()

private fun String.toPerson(headersMap: Map<String, Int>): Person {
    val columns = split(";")
    return Person(
        name = columns[headersMap["Name"]!!],
        email = columns[headersMap["E-mail"]!!],
        gender = columns[headersMap["Sex"]!!].let { if (it == "man") Gender.MALE else Gender.FEMALE },
        retired = columns[headersMap["Retired"]!!] == "true"
    )
}

private fun String.mapEventHeaders(): Map<String, Int> = split(";")
    .mapIndexed { index, header -> header to index }
    .toMap()

fun List<String>.mapSeasonName(): String {
    val headers = first().mapEventHeaders()
    return this[1].split(";")[headers["Sesong"]!!]
}

fun String.mapEventParticipant(headers: Map<String, Int>): EventCsv = split(";").let {
    try {
        return EventCsv(
            seasonName = it[headers["Sesong"]!!],
            eventName = it[headers["Øvelse"]!!],
            date = LocalDate.parse(it[headers["Dato"]!!], DateTimeFormatter.ISO_DATE),
            eventCategory = it[headers["Kategori"]!!],
            participantName = it[headers["Name"]!!],
            participantScore = it[headers["Score"]!!],
            participantRank = it[headers["Rank"]!!].toInt(),
            participantGender = if (it[headers["Kjønn"]!!] == "G") Gender.MALE else Gender.FEMALE,
        )
    } catch (e: Exception) {
        throw UnknownInternalServerError("errorMessage: ${e.message}. CsvRow: $it",
            headers.map { header -> header.key to it.getOrNull(header.value) })
    }
}

fun List<String>.mapEvents(): Map<String, List<EventCsv>> {
    val headers = first().mapEventHeaders()
    return subList(1, size).map { it.mapEventParticipant(headers) }.groupBy { it.eventName }
}

fun String.getSeasonStartYear(): Int =
    """(?<startYear>\d{4})-\d{4}""".toRegex().find(this)!!.groups["startYear"]!!.value.toInt()


data class EventCsv(
    val seasonName: String,
    val eventName: String,
    val date: LocalDate,
    val eventCategory: String,
    val participantName: String,
    val participantScore: String,
    val participantRank: Int,
    val participantGender: Gender
)