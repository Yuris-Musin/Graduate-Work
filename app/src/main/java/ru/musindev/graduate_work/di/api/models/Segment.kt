package ru.musindev.graduate_work.di.api.models

data class Segment(
    val departure: String,
    val arrival: String,
    val thread: ThreadInfo
)