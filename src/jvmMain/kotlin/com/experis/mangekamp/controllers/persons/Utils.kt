package com.experis.mangekamp.controllers.persons

import dto.GenderDto
import dto.PersonDto
import com.experis.mangekamp.models.Gender
import com.experis.mangekamp.models.Person

fun Person.toDto(): PersonDto = PersonDto(
    name = name,
    email = email,
    gender = if (gender == Gender.MALE) GenderDto.MALE else GenderDto.FEMALE,
    retired = retired,
    id = id
)

fun PersonDto.toModel(): Person = Person(
    name = name,
    email = email,
    gender = if (gender == GenderDto.MALE) Gender.MALE else Gender.FEMALE,
    retired = retired,
    id = id
)

fun Gender.toDto(): GenderDto = GenderDto.valueOf(this.name)

fun GenderDto.toModel(): Gender = Gender.valueOf(this.name)