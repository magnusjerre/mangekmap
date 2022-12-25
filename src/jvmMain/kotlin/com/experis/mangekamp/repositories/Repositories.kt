package com.experis.mangekamp.repositories

import com.experis.mangekamp.models.AdminUser
import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Participant
import com.experis.mangekamp.models.Season
import com.experis.mangekamp.models.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : JpaRepository<Event, Long> {
    fun findAllBySeasonId(seasonId: Long): List<Event>
    fun findBySeasonIdAndTitle(seasonId: Long, name: String): Event?
}

@Repository
interface CategoryRepository : JpaRepository<Category, Long>

@Repository
interface ParticipantRepository : JpaRepository<Participant, Long> {
    @Modifying
    fun deleteByIdEventIdAndIdPersonIdIn(eventId: Long, personId: List<Long>): Long

    fun existsByIdEventIdAndIdPersonName(eventId: Long, name: String): Boolean

    fun findAllByIdPersonId(personId: Long): List<Participant>
    fun findAllByIdPersonIdAndIdEventSeasonStartYear(personId: Long, startYear: Int): List<Participant>
    fun findAllByIdPersonIdInAndIdEventSeasonStartYearAndIdEventSeasonIdIsNot(personIds: List<Long>, startYear: Int, seasonId: Long): List<Participant>
    fun findAllByIdPersonIdAndIdEventSeasonIdIsNot(personId: Long, seasonId: Long): List<Participant>
}

@Repository
interface SeasonRepository : JpaRepository<Season, Long> {
    fun findByName(name: String): Season?
}

@Repository
interface PersonRepository : JpaRepository<Person, Long> {
    fun findAllByRetired(retired: Boolean): List<Person>
}

@Repository
interface AdminUserRepository : JpaRepository<AdminUser, Long> {
    fun findByUsername(username: String?): AdminUser?
}