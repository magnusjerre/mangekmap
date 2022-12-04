package com.experis.mangekamp

import org.slf4j.Logger

fun Logger.logPerformance(startTime: Long, message: String): Long {
    val now = System.currentTimeMillis()
    this.info("$message (${now - startTime} ms)")
    return now
}