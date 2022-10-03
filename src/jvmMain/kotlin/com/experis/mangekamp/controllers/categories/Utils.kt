package com.experis.mangekamp.controllers.categories

import com.experis.mangekamp.models.Category
import dto.CategoryDto

fun Category.toDto(): CategoryDto = CategoryDto(
    name = name,
    color = color,
    id = id!!
)