package ru.musindev.graduate_work.views.search

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import ru.musindev.graduate_work.API
import ru.musindev.graduate_work.databinding.FragmentSearchBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val apiKey = API.KEY
    private val client = OkHttpClient()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SearchViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val selectedDate = "$year-${month + 1}-$day"
                    binding.btnDate.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        viewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)

        // Наблюдаем за результатами
        viewModel.schedule.observe(viewLifecycleOwner) { segments ->
            if (segments.isEmpty()) {
                binding.tvResult.text = "Ничего не найдено"
            } else {
                val resultText = segments.joinToString("\n") { segment ->
                    """
                    Отправление: ${segment.departure}
                    Прибытие: ${segment.arrival}
                    Маршрут: ${segment.thread.title}

                    """.trimIndent()
                }
                binding.tvResult.text = resultText
            }
        }

        // Наблюдаем за ошибками
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            binding.tvResult.text = "Ошибка: $errorMessage"
        }

        binding.btnSearch.setOnClickListener {
            // Получаем введенные пользователем названия городов
            val cityFrom = binding.etFrom.text.toString().trim()
            val cityTo = binding.etTo.text.toString().trim()

            // Проверяем, что оба поля заполнены
            if (cityFrom.isEmpty() || cityTo.isEmpty()) {
                binding.tvResult.text = "Введите названия станций"
                return@setOnClickListener
            }

            // Получаем текущую или выбранную дату
            val selectedDate = if (binding.btnDate.text.isEmpty()) {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            } else {
                binding.btnDate.text.toString()
            }

            // Выполняем поиск кодов городов и запрос расписания
            fetchCityCode(cityFrom) { fromCode ->
                if (fromCode == null) {
                    requireActivity().runOnUiThread {
                        binding.tvResult.text = "Код города отправления не найден"
                    }
                    return@fetchCityCode
                }

                fetchCityCode(cityTo) { toCode ->
                    if (toCode == null) {
                        requireActivity().runOnUiThread {
                            binding.tvResult.text = "Код города назначения не найден"
                        }
                        return@fetchCityCode
                    }

                    // Если коды найдены, выполняем запрос расписания
                    requireActivity().runOnUiThread {
                        viewModel.fetchSchedule(apiKey, fromCode, toCode, selectedDate)
                    }
                }
            }
        }
    }

    private fun fetchCityCode(query: String, callback: (String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Формируем URL с пользовательским вводом
                val url = "https://suggests.rasp.yandex.net/all_suggests?format=old&part=${query}"
                val request = Request.Builder()
                    .url(url)
                    .build()

                Log.d("SearchFragment", "Запрос отправлен на URL: $url") // Логирование URL

                // Выполняем запрос
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("SearchFragment", "Ошибка в запросе: ${response.code}")
                        callback(null)
                        return@launch
                    }

                    // Парсим JSON-ответ
                    val responseData = response.body?.string()
                    Log.d("SearchFragment", "Ответ: $responseData") // Логирование ответа от API

                    if (responseData != null) {
                        try {
                            // Парсим массив данных (первый элемент - null, второй - массив)
                            val jsonArray = JSONArray(responseData).getJSONArray(1)

                            for (i in 0 until jsonArray.length()) {
                                val item = jsonArray.getJSONArray(i)
                                val cityName = item.getString(1)
                                val code = item.getString(0)

                                // Логирование каждого города
                                Log.d("SearchFragment", "Город: $cityName, Код: $code")

                                // Сравниваем имя города с запросом
                                if (cityName.equals(query, ignoreCase = true)) {
                                    callback(code)
                                    return@launch
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(
                                "SearchFragment",
                                "Ошибка при парсинге JSON: ${e.localizedMessage}"
                            )
                            callback(null)
                        }
                    }

                    callback(null) // Если код не найден
                }
            } catch (e: Exception) {
                Log.e("SearchFragment", "Ошибка запроса: ${e.localizedMessage}")
                callback(null)
            }
        }
    }

}