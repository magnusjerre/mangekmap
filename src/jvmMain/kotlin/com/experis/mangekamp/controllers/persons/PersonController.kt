package com.experis.mangekamp.controllers.persons

import ApiPersons
import com.experis.mangekamp.exceptions.ResourceNotFoundException
import com.experis.mangekamp.models.Gender
import com.experis.mangekamp.repositories.PersonRepository
import dto.GenderDto
import dto.PersonDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PersonController(
    private val personRepository: PersonRepository
) {
    @GetMapping(ApiPersons.BASE_PATH)
    fun getPersons(@RequestParam includeRetired: Boolean? = false): List<PersonDto> {
        val persons = if (includeRetired == true)
            personRepository.findAll()
        else
            personRepository.findAllByRetired(false)

        return persons.map {
            PersonDto(
                name = it.name,
                email = it.email,
                gender = if (it.gender == Gender.MALE) GenderDto.MALE else GenderDto.FEMALE,
                retired = it.retired,
                id = it.id
            )
        }
    }

    @PostMapping(ApiPersons.BASE_PATH)
    fun createPerson(@RequestBody dto: PersonDto): PersonDto =
        personRepository.save(dto.toModel().apply { id = null }).toDto()

    @GetMapping(ApiPersons.ID)
    fun getPerson(@PathVariable id: Long): PersonDto =
        personRepository.findById(id).orElseThrow { ResourceNotFoundException("Person with id $id not found") }.toDto()

    @PutMapping(ApiPersons.ID)
    fun putPerson(@PathVariable id: Long, @RequestBody changes: PersonDto): PersonDto {
        val person = personRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Person with id $id not found")
        }
        person.name = changes.name
        person.email = changes.email
        person.gender = changes.gender.toModel()
        person.retired = changes.retired

        personRepository.save(person)

        return person.toDto()
    }
}