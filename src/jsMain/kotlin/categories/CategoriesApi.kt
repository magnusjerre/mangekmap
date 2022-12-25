package categories

import ApiCategories
import dto.CategoryDto
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

suspend fun getCategories(): List<CategoryDto> {
    val response = window.fetch(ApiCategories.BASE_PATH).await();

    return if (response.ok) {
        Json.decodeFromString(response.text().await())
    } else {
        throw Exception("Error getting catgories: ${response.status}-${response.statusText}: ${response.text().await()}")
    }
}
