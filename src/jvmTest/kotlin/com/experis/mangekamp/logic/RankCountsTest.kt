package com.experis.mangekamp.logic

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RankCountsTest {
    @Test
    fun `test rank count for same rank`() {
        RankCount(rank = 1, count = 2).compareTo(RankCount(rank = 1, count = 1)).shouldBe(-1)
        RankCount(rank = 1, count = 1).compareTo(RankCount(rank = 1, count = 2)).shouldBe(1)
        RankCount(rank = 1, count = 1).compareTo(RankCount(rank = 1, count = 1)).shouldBe(0)
    }

    @Test
    fun `test RankCount for different rank`() {
        RankCount(rank = 1, count = 2).compareTo(RankCount(rank = 2, count = 5)).shouldBe(-1)
        RankCount(rank = 2, count = 5).compareTo(RankCount(rank = 1, count = 1)).shouldBe(1)
        RankCount(rank = 1, count = 1).compareTo(RankCount(rank = 1, count = 1)).shouldBe(0)
    }

    @Test
    fun `test RankCounts having the exact same content`() {
        val first = RankCounts(listOf(RankCount(1, 1), RankCount(2, 1)))
        val second = RankCounts(listOf(RankCount(1, 1), RankCount(2, 1)))
        first.compareTo(second).shouldBe(0)
        second.compareTo(first).shouldBe(0)
    }

    @Test
    fun `test RankCounts should sort automatically`() {
        RankCounts(listOf(RankCount(3, 2), RankCount(1, 3), RankCount(2, 4)))
            .rankCounts
            .shouldBe(listOf(RankCount(1, 3), RankCount(2, 4), RankCount(3, 2)))
    }

    @Test
    fun `test RankCounts should choose the one with the most first places`() {
        val winner = RankCounts(listOf(RankCount(1, 3), RankCount(5, 3)))
        val loser = RankCounts(listOf(RankCount(1, 1), RankCount(2, 3)))
        winner.compareTo(loser).shouldBe(-1)
        loser.compareTo(winner).shouldBe(1)
    }

    @Test
    fun `test RankCounts when multiple ranks are the same then it should look for the first difference`(){
        val winner = RankCounts(listOf(
            RankCount(1, 1), RankCount(2, 1), RankCount(3, 2)
        ))
        val loser = RankCounts(listOf(
            RankCount(1, 1), RankCount(2, 1), RankCount(3, 1)
        ))
        winner.compareTo(loser).shouldBe(-1)
        loser.compareTo(winner).shouldBe(1)
    }

    @Test
    fun `test RankCounts when they are the same but one has fewer RankCount-objects`() {
        val winner = RankCounts(listOf(
            RankCount(1, 1), RankCount(2, 1), RankCount(3, 2), RankCount(5, 1)
        ))
        val loser = RankCounts(listOf(
            RankCount(1, 1), RankCount(2, 1)
        ))
        winner.compareTo(loser).shouldBe(-1)
        loser.compareTo(winner).shouldBe(1)
    }

    @Test
    fun `test RankCounts should handle when one or both is empty`() {
        val nonEmpty = RankCounts(listOf(RankCount(1, 1)))
        val empty = RankCounts(emptyList())
        nonEmpty.compareTo(empty).shouldBe(-1)
        empty.compareTo(nonEmpty).shouldBe(1)

        empty.compareTo(empty).shouldBe(0)
    }
}