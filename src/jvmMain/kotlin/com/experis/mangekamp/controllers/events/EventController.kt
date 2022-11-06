package com.experis.mangekamp.controllers.events

import com.experis.mangekamp.exceptions.ResourceNotFoundException
import com.experis.mangekamp.models.Participant
import com.experis.mangekamp.models.ParticipantId
import com.experis.mangekamp.repositories.CategoryRepository
import com.experis.mangekamp.repositories.EventRepository
import com.experis.mangekamp.repositories.ParticipantRepository
import com.experis.mangekamp.repositories.PersonRepository
import dto.EventDto
import dto.EventPostDto
import dto.ParticipantDto
import dto.ParticipantPostDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/events")
class EventController(
    private val eventRepository: EventRepository,
    private val categoryRepository: CategoryRepository,
    private val personRepository: PersonRepository,
    private val participantRepository: ParticipantRepository
) {

    @GetMapping
    fun getEvents(@RequestParam seasonId: Long?, @RequestParam includeParticipants: Boolean?): List<EventDto> =
        if (seasonId == null)
            eventRepository.findAll().map { it.toDto(includeParticipants = includeParticipants == true) }
        else
            eventRepository.findAllBySeasonId(seasonId)
                .map { it.toDto(includeParticipants = includeParticipants == true) }


    @GetMapping("{id}")
    fun getEvent(@PathVariable id: Long): EventDto =
        eventRepository.findByIdOrNull(id)?.toDto() ?: throw ResourceNotFoundException("Event with id $id not found")

    @PostMapping
    fun postNewEvent(@RequestBody event: EventPostDto): EventDto = eventRepository.save(event.toModel {
        categoryRepository.findByIdOrNull(event.categoryId)
            ?: throw ResourceNotFoundException("Category with id ${event.categoryId} not found")
    }
    ).toDto()

    @PatchMapping("{id}")
    fun patchEvent(@PathVariable id: Long, @RequestBody event: EventPostDto): EventDto {
        val existingEvent =
            eventRepository.findByIdOrNull(id) ?: throw ResourceNotFoundException("Event with id $id not found")
        val category = if (event.categoryId == existingEvent.category.id)
            existingEvent.category
        else
            categoryRepository.findByIdOrNull(event.categoryId) ?: throw ResourceNotFoundException("Cateogry with id ${event.categoryId} not found")

        existingEvent.title = event.title
        existingEvent.date = LocalDate.parse(event.date, DateTimeFormatter.ISO_DATE)
        existingEvent.venue = event.venue
        existingEvent.category = category
        existingEvent.isTeamBased = event.isTeamBased

        return eventRepository.save(existingEvent).toDto()
    }

    @Transactional
    @PatchMapping("{eventId}/participants")
    fun patchParticipants(@PathVariable eventId: Long, @RequestBody body: List<ParticipantPostDto>): EventDto {
        val event = eventRepository.findByIdOrNull(eventId)
            ?: throw ResourceNotFoundException("Event with id $eventId not found")
        val requestPersonsIds = body.map(ParticipantPostDto::personId).toSet()
        val existingPersonsIds = event.participants.map { it.id.person.id }.toSet()
        val newPersonsIds = requestPersonsIds - existingPersonsIds
        val patchExistingPersonIds = requestPersonsIds - newPersonsIds
        val removeExistingPersonIds = (existingPersonsIds - requestPersonsIds).filterNotNull()

        val personsToAddAsParticipants = personRepository.findAllById(newPersonsIds)
        val newParticipants = personsToAddAsParticipants.map { np ->
            body.find { pp -> np.id == pp.personId }!!.let { pp ->
                Participant(
                    rank = pp.rank ?: 0,
                    score = pp.score ?: "",
                    isAttendanceOnly = pp.isAttendanceOnly ?: false,
                    id = ParticipantId(
                        person = np,
                        event = event
                    ),
                    teamNumber = pp.teamNumber,
                )
            }
        }
        // Må lagres utenfor event-objektet, vil ellers kaste en javax.persistence.EntityNotFoundException:
        // Unable to find com.experis.mangekamp.models.Participant with id com.experis.mangekamp.models.ParticipantId@29f78030
        participantRepository.saveAllAndFlush(newParticipants)
        // Oppdater eksisterende particpants
        event.participants.filter { patchExistingPersonIds.contains(it.id.person.id) }.forEach {
            val patchedParticipant = body.find { pp -> pp.personId == it.id.person.id }!!
            it.rank = patchedParticipant.rank ?: it.rank
            it.isAttendanceOnly = patchedParticipant.isAttendanceOnly ?: it.isAttendanceOnly
            it.score = patchedParticipant.score ?: it.score
            it.teamNumber = patchedParticipant.teamNumber ?: it.teamNumber
        }
        eventRepository.save(event)

        participantRepository.deleteByIdEventIdAndIdPersonIdIn(eventId, removeExistingPersonIds.toList())
        participantRepository.flush()

        // Legger til de nye deltakerne slik at returdtoen inneholder disse også
        event.participants += newParticipants
        event.participants = event.participants.filterNot { removeExistingPersonIds.contains(it.id.person.id) }

        return event.toDto(includeParticipants = true)
    }

    @DeleteMapping("{eventId}/participants")
    @Transactional
    fun deleteParticipantsByPersonId(@PathVariable eventId: Long, @RequestBody personIds: List<Long>): EventDto {
        if (!eventRepository.existsById(eventId)) {
            throw ResourceNotFoundException("Event with id $eventId not found")
        }

        participantRepository.deleteByIdEventIdAndIdPersonIdIn(eventId, personIds)
        participantRepository.flush()   // Uten flush vil neste kall inkludere de deltakerne som ble slettet

        return eventRepository.findByIdOrNull(eventId)?.toDto(includeParticipants = true)!!
    }

    @DeleteMapping("{eventId}")
    @Transactional
    fun deleteEvent(@PathVariable eventId: Long) {
        val event = eventRepository.findByIdOrNull(eventId) ?: throw ResourceNotFoundException("Event with id $eventId not found")
        participantRepository.deleteAll(event.participants)
        eventRepository.flush()
        eventRepository.deleteById(eventId)
        eventRepository.flush()
    }

    @GetMapping("{personId}/startYear/{startYear}")
    fun getAllEvents(@PathVariable personId: Long, @PathVariable startYear: Int): List<ParticipantDto> {
        val participants = participantRepository.findAllByIdPersonIdAndIdEventSeasonStartYear(personId, startYear)

        return participants.map { it.toDto() }
    }

    @GetMapping("{personId}/notSeason/{seasonId}")
    fun getAllEvents2(@PathVariable personId: Long, @PathVariable seasonId: Long): List<ParticipantDto> {
        val participants = participantRepository.findAllByIdPersonIdAndIdEventSeasonIdIsNot(personId, seasonId)

        return participants.map { it.toDto() }
    }
}