package com.experis.mangekamp.services

import com.experis.mangekamp.repositories.EventRepository
import com.experis.mangekamp.repositories.ParticipantRepository
import org.springframework.stereotype.Service

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val participantRepository: ParticipantRepository,
) {
    fun saveParticipants(eventId: Long, participants: List<Participant>) {

    }

}

class Participant(
    val personId: Int,
    val rank: Int? = null,
    val score: String? = null,
)