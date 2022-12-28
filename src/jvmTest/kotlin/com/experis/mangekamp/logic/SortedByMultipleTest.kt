package com.experis.mangekamp.logic

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SortedByMultipleTest {

    @Test
    fun shouldSortCorrectly3() {
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
            .sortedWithMultiple(Comparator { o1, o2 ->
                o1.first.compareTo(o2.first)
            }, Comparator { o1, o2 ->
                o1.second.compareTo(o2.second)
            }, Comparator { o1, o2 ->
                o1.third.compareTo(o2.third)
            }).makeFlat()
            .map { "${it.order}:${it.obj.first}-${it.obj.second}-${it.obj.third}" }
        sorted.shouldBe(
            listOf(
                "1:1-a-1",
                "2:1-a-2",
                "3:1-b-1",
                "4:1-b-2",
                "5:1-b-3",
                "5:1-b-3",
                "6:2-a-1",
                "7:2-a-2",
                "8:2-b-1",
                "9:2-b-2",
                "9:2-b-2",
            )
        )
    }
}