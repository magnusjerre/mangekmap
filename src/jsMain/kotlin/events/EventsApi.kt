package events

import ApiEvents
import dto.EventDto
import dto.EventPostDto
import dto.ParticipantPostDto
import dto.PersonEventsDto
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import replacePathVariables

suspend fun getEvent(id: Long): EventDto {
    val response = window.fetch(ApiEvents.ID.replacePathVariables(id)).await()

    if (response.ok) {
        return Json.decodeFromString(response.text().await())
    } else {
        throw Exception("Oh no")
    }
}

suspend fun patchParticipants(id: Long, participants: List<Long>) {
    val response = window.fetch(ApiEvents.ID_PARTICIPANTS.replacePathVariables(id), RequestInit(
        method = "PATCH",
        body = Json.encodeToJsonElement(participants),
        headers = Headers().apply { append("Content-Type", "application/json;charset=UTF-8") }
    )).await()

    if (!response.ok) {
        throw Exception("Oh no")
    }
}

suspend fun patchParticipantsResults(id: Long, participants: List<ParticipantPostDto>): EventDto {
    val response = window.fetch(ApiEvents.ID_PARTICIPANTS_RESULTS.replacePathVariables(id), RequestInit(
        method = "PATCH",
        body = Json.encodeToJsonElement(participants),
        headers = Headers().apply { append("Content-Type", "application/json;charset=UTF-8") }
    )).await()

    if (response.ok) {
        return Json.decodeFromString(response.text().await())
    } else {
        throw Exception("Oh no")
    }
}

suspend fun patchEvent(id: Long, event: EventPostDto): EventDto {
    val response = window.fetch(ApiEvents.ID.replacePathVariables(id), RequestInit(
        method = "PATCH",
        body = Json.encodeToJsonElement(event),
        headers = Headers().apply { append("Content-Type", "application/json;charset=UTF-8") }
    )).await()

    if (response.ok) {
        return Json.decodeFromString(response.text().await())
    } else {
        throw Exception("Oh no")
    }
}

suspend fun deleteEvent(id: Long) {
    val response = window.fetch(ApiEvents.ID.replacePathVariables(id), RequestInit(
        method = "DELETE"
    )).await()

    if (!response.ok) {
        throw Exception("oh no")
    }
}

suspend fun getParticipations(personId: Long): PersonEventsDto {
    val response = window.fetch(ApiEvents.PARTICIPATIONS_PERSONID.replacePathVariables(personId)).await()

    if (!response.ok)
        throw Exception("oh no")

    return Json.decodeFromString(response.text().await())
}
