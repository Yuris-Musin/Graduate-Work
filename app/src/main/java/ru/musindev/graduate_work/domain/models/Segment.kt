package ru.musindev.graduate_work.domain.models

data class Segment(
    val departure: String,
    val arrival: String,
    val thread: ThreadInfo,
    val departureTimeMillis: Long,
    val arrivalTimeMillis: Long
)