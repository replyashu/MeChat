package com.ashu.yalchat.global

import java.util.Date

fun Date.isWithinPast(minutes: Int): Boolean {
    val now = Date()
    val timeAgo = Date(now.time - (60 * minutes * 1000))
    val range = timeAgo..now
    return range.contains(this)
}