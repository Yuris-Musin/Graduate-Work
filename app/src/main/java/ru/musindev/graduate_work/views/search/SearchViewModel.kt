package ru.musindev.graduate_work.views.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.musindev.graduate_work.data.api.YandexRaspApi
import ru.musindev.graduate_work.domain.models.ScheduleResponse
import ru.musindev.graduate_work.domain.models.Segment
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val yandexRaspApi: YandexRaspApi
) : ViewModel() {

    private val _schedule = MutableStateFlow<List<Segment>>(emptyList())
    val schedule: StateFlow<List<Segment>> get() = _schedule.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error.asStateFlow()

    fun fetchSchedule(apiKey: String, from: String, to: String, date: String) {
        _schedule.value = emptyList() // Очистка предыдущих данных
        _error.value = null // Очистка предыдущей ошибки
        viewModelScope.launch {
            yandexRaspApi.getSchedule(apiKey, from, to, date, "bus").enqueue(object : Callback<ScheduleResponse> {
                override fun onResponse(call: Call<ScheduleResponse>, response: Response<ScheduleResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        _schedule.value = response.body()?.segments ?: emptyList()
                    } else {
                        _error.value = "Ошибка сервера или данные отсутствуют"
                    }
                }

                override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                    _error.value = "Не удалось загрузить данные: ${t.message}"
                }
            })
        }
    }
}