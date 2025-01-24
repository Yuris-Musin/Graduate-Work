package ru.musindev.graduate_work.views.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.musindev.graduate_work.di.api.YandexRaspApi
import ru.musindev.graduate_work.di.api.models.ScheduleResponse
import ru.musindev.graduate_work.di.api.models.Segment
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val yandexRaspApi: YandexRaspApi
) : ViewModel() {

    private val _schedule = MutableLiveData<List<Segment>>()
    val schedule: LiveData<List<Segment>> get() = _schedule

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchSchedule(apiKey: String, from: String, to: String, date: String) {
        yandexRaspApi.getSchedule(apiKey, from, to, date, "bus").enqueue(object : Callback<ScheduleResponse> {
            override fun onResponse(call: Call<ScheduleResponse>, response: Response<ScheduleResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    _schedule.postValue(response.body()?.segments ?: emptyList())
                } else {
                    Log.e("API_ERROR", "Response code: ${response.code()}, message: ${response.message()}")
                    _error.postValue("Ошибка сервера или данные отсутствуют")
                }
            }

            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                Log.e("API_ERROR", "Failure: ${t.message}", t)
                _error.postValue("Не удалось загрузить данные: ${t.message}")
            }
        })
    }
}