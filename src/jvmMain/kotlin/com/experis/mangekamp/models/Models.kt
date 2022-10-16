package com.experis.mangekamp.models

import java.io.Serializable
import java.time.LocalDate
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class Event(
    var date: LocalDate,
    var title: String,
    @ManyToOne(targetEntity = Category::class)
    var category: Category,
    var venue: String,
    @OneToMany(mappedBy = "id.event")
    var participants: Collection<Participant>,
    var isTeamBased: Boolean = false,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) {
    @ManyToOne(targetEntity = Season::class)
    lateinit var season: Season
}

@Entity
class Category(
    var name: String,
    var color: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) {
    @OneToMany(mappedBy = "category")
    lateinit var events: Collection<Event>

    override fun toString(): String = "${Category::class.simpleName}(id=$id, name=$name)"
}

@Entity
class Participant(
    var rank: Int,
    var score: String,
    @EmbeddedId
    var id: ParticipantId
)

@Embeddable
class ParticipantId(
    @ManyToOne(targetEntity = Person::class, cascade = [CascadeType.ALL])
    val person: Person,
    @ManyToOne(targetEntity = Event::class, cascade = [CascadeType.ALL])
    val event: Event
) : Serializable

@Entity
class Season(
    @OneToMany(mappedBy = "season")
    var events: MutableList<Event>,
    var name: String,
    var startYear: Int,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)

@Entity
class Person(
    var name: String,
    var email: String,
    var gender: Gender,
    var retired: Boolean,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) {
    @OneToMany(mappedBy = "id.person", fetch = FetchType.LAZY)
    lateinit var participants: Collection<Participant>
}

@Entity
class AdminUser(
    @Column(nullable = false, unique = true)
    var username: String,
    var email: String,
    var name: String,
    var passwordDigest: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)

enum class Gender {
    MALE, FEMALE
}