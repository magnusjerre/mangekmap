package persons

import dto.PersonDto
import browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit

private const val personApiBasePath = "/api/persons"

suspend fun getPerson(id: Long?): PersonDto? {
    val response = window.fetch("$personApiBasePath/$id").await()
    return if (response.ok) {
        Json.decodeFromString(response.text().await())
    } else {
        console.log("getPerson failed", response)
        null
    }
}

suspend fun postPerson(person: PersonDto): PersonDto {
    val response = window.fetch(
        personApiBasePath,
        RequestInit(
            method = "POST",
            body = Json.encodeToJsonElement(person),
            headers = Headers().apply { append("Content-Type", "application/json;charset=UTF-8") })
    ).await()
    return if (response.ok) {
        Json.decodeFromString(response.text().await())
    } else {
        throw Exception("Oh no")
    }
}

suspend fun putPerson(person: PersonDto): PersonDto {
    val response = window.fetch(
        "$personApiBasePath/${person.id}",
        RequestInit(
            method = "PUT",
            body = Json.encodeToJsonElement(person),
            headers = Headers().apply { append("Content-Type", "application/json;charset=UTF-8") })
    ).await()
    return if (response.ok) {
        Json.decodeFromString(response.text().await())
    } else {
        throw Exception("Oh no")
    }
}

// Kommentar for egen del: Var nødt til å ta inn kotlinx-json greier og bruke
// Json.decodeFromString for at det skulle fungere
suspend fun fetchPersons(includeRetired: Boolean = false): List<PersonDto> {
    val response = kotlinx.browser.window.fetch("$personApiBasePath?includeRetired=$includeRetired").await()
    return if (response.ok) {
        val jsonContent: String = response.text().await()
        val parsed = Json.decodeFromString<List<PersonDto>>(jsonContent)
        parsed
    } else {
        emptyList()
    }
}