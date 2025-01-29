package ru.musindev.graduate_work.data.local.request

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_requests")
data class SearchRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityFrom: String,
    val cityTo: String
)