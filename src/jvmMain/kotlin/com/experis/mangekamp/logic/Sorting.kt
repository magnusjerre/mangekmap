package com.experis.mangekamp.logic

class EquallyOrdered<T>(objects: List<T>): Iterable<T> {
    private val objects: MutableList<T> = objects.toMutableList()

    override fun iterator(): Iterator<T> {
        return objects.iterator()
    }

    fun add(obj: T) {
        objects.add(obj)
    }

    fun list(): List<T> = objects.toList()

    fun size(): Int = objects.size
}

/**
 * @return a (possibly shorter) list, containing all elements, however, elements with the same order according to the
 * last comparator are bucketed into the same EquallyOrdered object.
 */
fun <T> List<T>.sortedWithComparators(vararg comparators: Comparator<T>): List<EquallyOrdered<T>> {
    var sortedList: List<EquallyOrdered<T>> = sortedWithSingleComparator(comparators.first())

    for (i in 1 until comparators.size) {
        sortedList = sortedList.map { equallyOrderedElements ->
            equallyOrderedElements.list().sortedWithSingleComparator(comparators[i])
        }.flatten().toMutableList()
    }
    return sortedList
}

/**
 * @return a (possibly shorter) list, containing all elements, however, elements with the same order according to the
 * comparator are bucketed into the same EquallyOrdered object.
 *
 * Will first apply the comparator to sort this list, then iterate through the sorted list using the comparator again to
 * determine whether some elements are considered equal. Equal elements will then be part of the same EquallyOrdered entry.
 */
private fun <T> List<T>.sortedWithSingleComparator(comparator: Comparator<T>): List<EquallyOrdered<T>> {
    if (isEmpty()) return listOf()
    if (size == 1) return listOf(EquallyOrdered(this))

    val sorted = sortedWith(comparator)
    val output = mutableListOf(EquallyOrdered(listOf(sorted.first())))
    for (index in 1 until sorted.size) {
        val previous = sorted[index - 1]
        val current = sorted[index]
        if (comparator.compare(previous, current) == 0) {
            output.last().add(current)
        } else {
            output.add(EquallyOrdered(listOf(current)))
        }
    }
    return output
}