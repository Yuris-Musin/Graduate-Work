package ru.musindev.graduate_work.di.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.musindev.graduate_work.di.api.models.ScheduleResponse

interface YandexRaspApi {
    @GET("v3.0/search/")
    fun getSchedule(
        @Query("apikey") apiKey: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("date") date: String,
        @Query("transport_types") transportTypes: String
    ): Call<ScheduleResponse>
}
