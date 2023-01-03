package com.experis.mangekamp.logic

import com.experis.mangekamp.models.Category
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class IsMangekjemperTest {

    private val kondisjon = Category("Kondisjon", "red", 1)
    private val ball = Category("Balløvelser", "green", 2)
    private val teknikk = Category("Teknikk", "blue", 3)

    @Test
    fun `Participant with enough events and enough unique categories should be mangekjemper`() {
        listOf(
            EventParticipation("Minigolf", ball, 1, 1),
            EventParticipation("Orientering", kondisjon, 2, 1),
            EventParticipation("Crossfit", kondisjon, 3, 1),
            EventParticipation("Frisbeegolf", teknikk, 4, 1),
            EventParticipation("Roing", kondisjon, 5, 1),
            EventParticipation("Poker", teknikk, 6, 1),
            EventParticipation("E-sport", teknikk, 7, 1),
            EventParticipation("Ski med blink", kondisjon, 8, 1),
        ).isMangekjemper(mangekjemerEventsRequirement = 8, categoryTypes = 3) shouldBe true
    }

    @Test
    fun `Participant with enough events and enough unique categories should be mangekjemper test 2`() {
        listOf(
            EventParticipation("Minigolf", ball, 1, 1),
            EventParticipation("Orientering", kondisjon, 2, 1),
            EventParticipation("Crossfit", kondisjon, 3, 1),
            EventParticipation("Frisbeegolf", teknikk, 4, 1),
            EventParticipation("Roing", kondisjon, 5, 1),
            EventParticipation("Poker", teknikk, 6, 1),
        ).isMangekjemper(mangekjemerEventsRequirement = 5, categoryTypes = 3) shouldBe true
    }

    @Test
    fun `Participant with too few events should not be mangekjemeper`() {
        listOf(
            EventParticipation("Minigolf", ball, 1, 1),
            EventParticipation("Orientering", kondisjon, 2, 1),
            EventParticipation("Crossfit", kondisjon, 3, 1),
            EventParticipation("Frisbeegolf", teknikk, 4, 1),
        ).isMangekjemper(mangekjemerEventsRequirement = 5) shouldBe false
    }

    @Test
    fun `Participant with enough events but too few categories should not be mangekjemeper`() {
        listOf(
            EventParticipation("Minigolf", ball, 1, 1),
            EventParticipation("Orientering", kondisjon, 2, 1),
            EventParticipation("Crossfit", kondisjon, 3, 1),
        ).isMangekjemper(mangekjemerEventsRequirement = 2, categoryTypes = 3) shouldBe false
    }

}