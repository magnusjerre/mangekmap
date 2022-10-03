package categories

import dto.CategoryDto
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private const val categoriesApiBasPath = "/api/categories"

suspend fun getCategories(): List<CategoryDto> {
    val response = window.fetch(categoriesApiBasPath).await();

    return if (response.ok) {
        Json.decodeFromString(response.text().await())
    } else {
        throw Exception("Error getting catgories: ${response.status}-${response.statusText}: ${response.text().await()}")
    }
}

suspend fun getCategory(id: Long): CategoryDto {
    val response = window.fetch("$categoriesApiBasPath/$id").await()

    return if(response.ok) {
        Json.decodeFromString(response.text().await())
    } else {
        throw Exception("Error getting category by id: ${response.status}-${response.statusText}: ${response.text().await()}")
    }
}