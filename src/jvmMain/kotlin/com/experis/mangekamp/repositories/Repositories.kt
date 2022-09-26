package com.experis.mangekamp.repositories

import com.experis.mangekamp.models.AdminUser
import com.experis.mangekamp.models.Category
import com.experis.mangekamp.models.Event
import com.experis.mangekamp.models.Participant
import com.experis.mangekamp.models.Season
import com.experis.mangekamp.models.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : JpaRepository<Event, Long>

@Repository
interface CategoryRepository : JpaRepository<Category, Long>

@Repository
interface ParticipantRepository : JpaRepository<Participant, Long>

@Repository
interface SeasonRepository : JpaRepository<Season, Long>

@Repository
interface PersonRepository : JpaRepository<Person, Long>

@Repository
interface AdminUserRepository : JpaRepository<AdminUser, Long> {
    fun findByUsername(username: String): AdminUser?
}