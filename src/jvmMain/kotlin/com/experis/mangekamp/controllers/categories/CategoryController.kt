package com.experis.mangekamp.controllers.categories

import com.experis.mangekamp.exceptions.ResourceNotFoundException
import com.experis.mangekamp.models.Category
import com.experis.mangekamp.repositories.CategoryRepository
import dto.CategoryDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/categories")
class CategoryController(
    private val categoryRepository: CategoryRepository
) {
    @GetMapping
    fun getCategories(): List<CategoryDto> = categoryRepository.findAll().map(Category::toDto)

    @GetMapping("{id}")
    fun getCategory(@PathVariable id: Long): CategoryDto =
        categoryRepository.findById(id).orElseThrow { ResourceNotFoundException("Category with id $id not found") }
            .toDto()
}