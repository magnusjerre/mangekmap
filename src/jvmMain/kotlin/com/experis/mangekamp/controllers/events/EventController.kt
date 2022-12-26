package com.experis.mangekamp.controllers.events

import ApiEvents
import com.experis.mangekamp.exceptions.ResourceNotFoundException
import com.experis.mangekamp.models.Participant
import com.experis.mangekamp.models.ParticipantId
import com.experis.mangekamp.repositories.CategoryRepository
import com.experis.mangekamp.repositories.EventRepository
import com.experis.mangekamp.repositories.ParticipantRepository
import com.experis.mangekamp.repositories.PersonRepository
import dto.EventDto
import dto.EventPostDto
import dto.ParticipantPostDto
import dto.PersonEventsDto
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
class EventController(
    private val eventRepository: EventRepository,
    private val categoryRepository: CategoryRepository,
    private val personRepository: PersonRepository,
    private val participantRepository: ParticipantRepository
) {
    @GetMapping(ApiEvents.BASE_PATH)
    fun getEvents(@RequestParam seasonId: Long?, @RequestParam includeParticipants: Boolean?): List<EventDto> =
        if (seasonId == null)
            eventRepository.findAll().map { it.toDto(includeParticipants = includeParticipants == true) }
        else
            eventRepository.findAllBySeasonId(seasonId)
                .map { it.toDto(includeParticipants = includeParticipants == true) }

    @PostMapping(ApiEvents.BASE_PATH)
    fun postNewEvent(@RequestBody event: EventPostDto): EventDto = eventRepository.save(event.toModel {
        categoryRepository.findByIdOrNull(event.categoryId)
            ?: throw ResourceNotFoundException("Category with id ${event.categoryId} not found")
    }
    ).toDto()

    @GetMapping(ApiEvents.ID)
    fun getEvent(@PathVariable id: Long): EventDto =
        eventRepository.findByIdOrNull(id)?.toDto() ?: throw ResourceNotFoundException("Event with id $id not found")

    @PatchMapping(ApiEvents.ID)
    fun patchEvent(@PathVariable id: Long, @RequestBody event: EventPostDto): EventDto {
        val existingEvent =
            eventRepository.findByIdOrNull(id) ?: throw ResourceNotFoundException("Event with id $id not found")
        val category = if (event.categoryId == existingEvent.category.id)
            existingEvent.category
        else
            categoryRepository.findByIdOrNull(event.categoryId)
                ?: throw ResourceNotFoundException("Cateogry with id ${event.categoryId} not found")

        existingEvent.title = event.title
        existingEvent.date = LocalDate.parse(event.date, DateTimeFormatter.ISO_DATE)
        existingEvent.venue = event.venue
        existingEvent.category = category
        existingEvent.isTeamBased = event.isTeamBased

        return eventRepository.save(existingEvent).toDto()
    }

    @DeleteMapping(ApiEvents.ID)
    @Transactional
    fun deleteEvent(@PathVariable id: Long) {
        val event = eventRepository.findByIdOrNull(id) ?: throw ResourceNotFoundException("Event with id $id not found")
        participantRepository.deleteAll(event.participants)
        eventRepository.flush()
        eventRepository.deleteById(id)
        eventRepository.flush()
    }

    @Transactional
    @PatchMapping(ApiEvents.ID_PARTICIPANTS)
    fun addRemoveParticipants(@PathVariable eventId: Long, @RequestBody personIds: Set<Long>) {
        val event = eventRepository.findByIdOrNull(eventId)
            ?: throw ResourceNotFoundException("Event with id $eventId not found")
        val existingPersonsIds = event.participants.map { it.id.person.id }.toSet()
        val newPersonsIds = personIds - existingPersonsIds
        val personsToAddAsParticipants = personRepository.findAllById(newPersonsIds)
        val newParticipants = personsToAddAsParticipants.map { newParticipant ->
            Participant(
                rank = 0,
                score = "",
                isAttendanceOnly = false,
                id = ParticipantId(person = newParticipant, event = event),
                teamNumber = null,
            )
        }
        // Må lagres utenfor event-objektet, vil ellers kaste en javax.persistence.EntityNotFoundException:
        // Unable to find com.experis.mangekamp.models.Participant with id com.experis.mangekamp.models.ParticipantId@29f78030
        participantRepository.saveAllAndFlush(newParticipants)

        val removeExistingPersonIds = (existingPersonsIds - personIds).filterNotNull()
        participantRepository.deleteByIdEventIdAndIdPersonIdIn(eventId, removeExistingPersonIds.toList())
        participantRepository.flush()
    }

    @Transactional
    @PatchMapping(ApiEvents.ID_PARTICIPANTS_RESULTS)
    fun patchParticipantsResults(@PathVariable eventId: Long, @RequestBody body: List<ParticipantPostDto>): EventDto {
        val event = eventRepository.findByIdOrNull(eventId)
            ?: throw ResourceNotFoundException("Event with id $eventId not found")

        body.forEach { participantDto ->
            val participant = event.participants.find { it.id.person.id == participantDto.personId }
                ?: throw ResourceNotFoundException("No person found for personId ${participantDto.personId}")
            participant.rank = participantDto.rank ?: participant.rank
            participant.isAttendanceOnly = participantDto.isAttendanceOnly ?: participant.isAttendanceOnly
            participant.score = participantDto.score ?: participant.score
            participant.teamNumber = participantDto.teamNumber ?: participant.teamNumber
        }
        eventRepository.save(event)

        return event.toDto(includeParticipants = true)
    }

    @GetMapping(ApiEvents.PARTICIPATIONS_PERSONID)
    fun getAllPersonEvents(@PathVariable personId: Long): PersonEventsDto = participantRepository
        .findAllByIdPersonId(personId)
        .takeIf { it.isNotEmpty() }
        ?.let {
            PersonEventsDto(
                personId = it.first().id.person.id!!,
                personName = it.first().id.person.name,
                events = it.map(Participant::toParticipantSimpleDto)
            )
        } ?: throw ResourceNotFoundException("No events for person found")
}