package events

import dto.EventDto
import dto.ParticipantPostDto
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private const val eventsApiBasePath = "/api/events"

suspend fun getEvent(id: Long): EventDto {
    val response = window.fetch("$eventsApiBasePath/$id").await()

    if (response.ok) {
        return Json.decodeFromString(response.text().await())
    } else {
        throw Exception("Oh no")
    }
}

suspend fun pathParticipants(id: Long, participants: List<ParticipantPostDto>): EventDto {
    val response = window.fetch("$eventsApiBasePath/$id/participants").await()

    if (response.ok) {
        return Json.decodeFromString(response.text().await())
    } else {
        throw Exception("Oh no")
    }
}