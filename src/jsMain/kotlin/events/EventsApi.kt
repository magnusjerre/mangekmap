package events

import dto.EventDto
import dto.ParticipantPostDto
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit

private const val eventsApiBasePath = "/api/events"

suspend fun getEvent(id: Long): EventDto {
    val response = window.fetch("$eventsApiBasePath/$id").await()

    if (response.ok) {
        return Json.decodeFromString(response.text().await())
    } else {
        throw Exception("Oh no")
    }
}

suspend fun patchParticipants(id: Long, participants: List<ParticipantPostDto>): EventDto {
    val response = window.fetch("$eventsApiBasePath/$id/participants", RequestInit(
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