package ru.musindev.graduate_work.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import ru.musindev.graduate_work.domain.models.ScheduleResponse

interface YandexRaspApi {
    @GET("v3.0/search/")
    suspend fun getSchedule(
        @Query("apikey") apiKey: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("date") date: String,
        @Query("transport_types") transportTypes: String
    ): ScheduleResponse
}
