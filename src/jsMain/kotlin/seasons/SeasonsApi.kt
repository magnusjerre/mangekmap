package seasons

import dto.EventDto
import dto.EventPostDto
import dto.SeasonDto
import dto.SeasonPostDto
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit

private const val seasonsApiBasePath = "/api/seasons"

suspend fun getSeasons(excludeEvents: Boolean): List<SeasonDto> {
    val result = window.fetch("$seasonsApiBasePath?excludeEvents=$excludeEvents").await()

    return if (result.ok) {
        Json.decodeFromString(result.text().await())
    } else {
        emptyList()
    }
}

suspend fun getSeason(id: Long, excludeEvents: Boolean): SeasonDto {
    val result = window.fetch("$seasonsApiBasePath/$id?excludeEvents=$excludeEvents").await()
    return if (result.ok) {
        Json.decodeFromString(result.text().await())
    } else {
        throw Exception("Error getting season: ${result.status}-${result.statusText}: ${result.text().await()}")
    }
}

suspend fun postSeason(seasonDto: SeasonPostDto): SeasonDto {
    val result = window.fetch(
        seasonsApiBasePath,
        RequestInit(
            method = "POST",
            body = Json.encodeToJsonElement(seasonDto),
            headers = Headers().apply { append("Content-Type", "application/json;charset=UTF-8") })
    ).await()

    if (result.ok) {
        return Json.decodeFromString(result.text().await())
    } else {
        throw Exception("Error posting season: ${result.status}-${result.statusText}: ${result.text().await()}")
    }
}

suspend fun putSeason(id: Long, seasonDto: SeasonPostDto): SeasonDto {
    val result = window.fetch(
        "$seasonsApiBasePath/$id",
        RequestInit(
            method = "PATCH",
            body = Json.encodeToJsonElement(seasonDto),
            headers = Headers().apply { append("Content-Type", "application/json;charset=UTF-8") })
    ).await()

    if (result.ok) {
        return Json.decodeFromString(result.text().await())
    } else {
        throw Exception("Error patching season: ${result.status}-${result.statusText}: ${result.text().await()}")
    }
}

suspend fun deleteSeason(id: Long) {
    val result = window.fetch("$seasonsApiBasePath/$id", RequestInit(method = "DELETE")).await()

    if (!result.ok) {
        throw Exception("Error deleting season: ${result.status}-${result.statusText}: ${result.text().await()}")
    }
}

suspend fun postSeasonEvent(id: Long, event: EventPostDto) {
    val result = window.fetch(
        "$seasonsApiBasePath/$id/events",
        RequestInit(
            method = "POST",
            body = Json.encodeToJsonElement(event),
            headers = Headers().apply { append("Content-Type", "application/json;charset=UTF-8") })
    ).await()

    if (result.ok) {
        return Json.decodeFromString(result.text().await())
    } else {
        throw Exception(
            "Error posting event: ${result.status}-${result.statusText}: ${result.text().await()}"
        )
    }
}