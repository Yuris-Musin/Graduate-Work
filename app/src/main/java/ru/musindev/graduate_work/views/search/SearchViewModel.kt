package ru.musindev.graduate_work.views.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.musindev.graduate_work.data.api.YandexRaspApi
import ru.musindev.graduate_work.domain.models.Segment
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val yandexRaspApi: YandexRaspApi
) : ViewModel() {

    private val _schedule = MutableStateFlow<List<Segment>>(emptyList())
    val schedule: StateFlow<List<Segment>> get() = _schedule.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error.asStateFlow()

    private val _from = MutableStateFlow<String?>(null)
    val from: StateFlow<String?> get() = _from.asStateFlow()

    private val _to = MutableStateFlow<String?>(null)
    val to: StateFlow<String?> get() = _to.asStateFlow()

    private val _date = MutableStateFlow<String?>(null)
    val date: StateFlow<String?> get() = _date.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    fun fetchSchedule(apiKey: String, from: String, to: String, date: String) {
        viewModelScope.launch {
            _from.value = from
            _to.value = to
            _date.value = date
            _schedule.value = emptyList() // Очистка предыдущих данных
            _error.value = null // Очистка предыдущей ошибки
            _isLoading.value = true
            try {
                val response = yandexRaspApi.getSchedule(apiKey, from, to, date, "bus")
                _schedule.value = response.segments
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Не удалось загрузить данные: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}