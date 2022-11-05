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
            SeasonSimplifiedEvent("Minigolf", ball, 1),
            SeasonSimplifiedEvent("Orientering", kondisjon, 2),
            SeasonSimplifiedEvent("Crossfit", kondisjon, 3),
            SeasonSimplifiedEvent("Frisbeegolf", teknikk, 4),
            SeasonSimplifiedEvent("Roing", kondisjon, 5),
            SeasonSimplifiedEvent("Poker", teknikk, 6),
            SeasonSimplifiedEvent("E-sport", teknikk, 7),
            SeasonSimplifiedEvent("Ski med blink", kondisjon, 8),
        ).isMangekjemper(mangekjemerEventsRequirement = 8, categoryTypes = 3) shouldBe true
    }

    @Test
    fun `Participant with enough events and enough unique categories should be mangekjemper test 2`() {
        listOf(
            SeasonSimplifiedEvent("Minigolf", ball, 1),
            SeasonSimplifiedEvent("Orientering", kondisjon, 2),
            SeasonSimplifiedEvent("Crossfit", kondisjon, 3),
            SeasonSimplifiedEvent("Frisbeegolf", teknikk, 4),
            SeasonSimplifiedEvent("Roing", kondisjon, 5),
            SeasonSimplifiedEvent("Poker", teknikk, 6),
        ).isMangekjemper(mangekjemerEventsRequirement = 5, categoryTypes = 3) shouldBe true
    }

    @Test
    fun `Participant with too few events should not be mangekjemeper`() {
        listOf(
            SeasonSimplifiedEvent("Minigolf", ball, 1),
            SeasonSimplifiedEvent("Orientering", kondisjon, 2),
            SeasonSimplifiedEvent("Crossfit", kondisjon, 3),
            SeasonSimplifiedEvent("Frisbeegolf", teknikk, 4),
        ).isMangekjemper(mangekjemerEventsRequirement = 5) shouldBe false
    }

    @Test
    fun `Participant with enough events but too few categories should not be mangekjemeper`() {
        listOf(
            SeasonSimplifiedEvent("Minigolf", ball, 1),
            SeasonSimplifiedEvent("Orientering", kondisjon, 2),
            SeasonSimplifiedEvent("Crossfit", kondisjon, 3),
        ).isMangekjemper(mangekjemerEventsRequirement = 2, categoryTypes = 3) shouldBe false
    }

}