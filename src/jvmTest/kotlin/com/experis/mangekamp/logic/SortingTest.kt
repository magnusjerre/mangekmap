package com.experis.mangekamp.logic

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SortingTest {
    @Test
    fun `Should sort using single comparator, bucketing elements considered equal`() {
        val sorted = listOf(1, 2, 1, 3, 4, 3, 5, 3, 6).sortedWithComparators(Comparator { o1, o2 -> o1.compareTo(o2) })
            .map { it.joinToString(",") { number -> "$number" } }
        sorted.shouldBe(
            listOf(
                "1,1",
                "2",
                "3,3,3",
                "4",
                "5",
                "6"
            )
        )
    }

    @Test
    fun `Should sort according to the comparator, bucketing only elements considered equal according to the last comparator`() {
        val sorted = listOf<Triple<Long, String, Int>>(
            Triple(1, "a", 2),
            Triple(2, "a", 1),
            Triple(2, "a", 2),
            Triple(1, "b", 1),
            Triple(1, "b", 2),
            Triple(1, "b", 3),
            Triple(2, "b", 2),
            Triple(2, "b", 1),
            Triple(1, "a", 1),
            Triple(1, "b", 3),
            Triple(2, "b", 2),
        )
            .sortedWithComparators(Comparator { o1, o2 ->
                o1.first.compareTo(o2.first)
            }, Comparator { o1, o2 ->
                o1.second.compareTo(o2.second)
            }, Comparator { o1, o2 ->
                o1.third.compareTo(o2.third)
            }).map { it.joinToString(",") { triple -> "${triple.first}-${triple.second}-${triple.third}" } }
        sorted.shouldBe(
            listOf(
                "1-a-1",
                "1-a-2",
                "1-b-1",
                "1-b-2",
                "1-b-3,1-b-3",
                "2-a-1",
                "2-a-2",
                "2-b-1",
                "2-b-2,2-b-2",
            )
        )
    }
}