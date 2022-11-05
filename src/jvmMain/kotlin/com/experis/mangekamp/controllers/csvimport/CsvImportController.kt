package com.experis.mangekamp.controllers.csvimport

import MANGEKJEMPER_REQUIRED_EVENTS
import com.experis.mangekamp.controllers.seasons.toDto
import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Participant
import com.experis.mangekamp.models.ParticipantId
import com.experis.mangekamp.models.Person
import com.experis.mangekamp.models.Season
import com.experis.mangekamp.repositories.CategoryRepository
import com.experis.mangekamp.repositories.EventRepository
import com.experis.mangekamp.repositories.ParticipantRepository
import com.experis.mangekamp.repositories.PersonRepository
import com.experis.mangekamp.repositories.SeasonRepository
import dto.SeasonDto
import java.nio.charset.Charset
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("api/import")
class CsvImportController(
    private val categoryRepository: CategoryRepository,
    private val eventRepository: EventRepository,
    private val participantRepository: ParticipantRepository,
    private val personRepository: PersonRepository,
    private val seasonRepository: SeasonRepository,
) {

    @PostMapping("persons")
    fun postImportPersons(@RequestBody csv: MultipartFile): Int {
        val lines = csv.resource.inputStream.use {
            it.bufferedReader(Charset.forName("UTF-8")).readLines()
        }
        val persons = lines.mapPersons()
        return personRepository.saveAllAndFlush(persons).size
    }

    @PostMapping("events")
    fun postImportEvents(@RequestBody csv: MultipartFile): SeasonDto {
        val lines = csv.resource.inputStream.use {
            it.bufferedReader(Charset.forName("UTF-8")).readLines()
        }

        val seasonName = lines.mapSeasonName()
        val seasonStartYear = seasonName.getSeasonStartYear()

        val season = seasonRepository.findByName(seasonName) ?: seasonRepository.saveAndFlush(
            Season(
                events = mutableListOf(),
                name = seasonName,
                startYear = seasonStartYear,
                mangekjemperRequiredEvents = MANGEKJEMPER_REQUIRED_EVENTS
            )
        )

        val events: Map<String, List<EventCsv>> = lines.mapEvents()

        val categories = categoryRepository.findAll()
        val allPersons = personRepository.findAll()

        fun getCategory(name: String): Category =
            categories.find { it.name.lowercase().startsWith(name.lowercase().substring(0, 1)) }!!

        fun getPerson(name: String): Person = allPersons.single { it.name.lowercase() == name.lowercase() }

        for (entry in events) {
            val event =
                eventRepository.findBySeasonIdAndTitle(season.id!!, entry.key) ?: eventRepository.saveAndFlush(
                    entry.value.first().let {
                        Event(
                            date = it.date,
                            title = it.eventName,
                            category = getCategory(it.eventCategory),
                            venue = it.eventName,
                            participants = mutableListOf(),
                            isTeamBased = false,
                        ).apply {
                            this.season = season
                        }
                    })

            for (eventCsv in entry.value) {
                if (!participantRepository.existsByIdEventIdAndIdPersonName(event.id!!, eventCsv.participantName)) {
                    participantRepository.save(
                        Participant(
                            rank = eventCsv.participantRank,
                            score = eventCsv.participantScore,
                            id = ParticipantId(
                                person = getPerson(eventCsv.participantName),
                                event = event
                            )
                        )
                    )
                }
            }
            participantRepository.flush()
        }

        return seasonRepository.findByIdOrNull(season.id!!)!!.toDto()
    }

}