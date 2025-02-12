package ru.musindev.graduate_work.views.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import ru.musindev.graduate_work.API
import ru.musindev.graduate_work.databinding.FragmentSearchBinding
import ru.musindev.graduate_work.data.local.db.AppDatabase
import ru.musindev.graduate_work.data.local.request.SearchRequest
import ru.musindev.graduate_work.views.adapters.ScheduleAdapter
import ru.musindev.graduate_work.views.calendar.CalendarDialogFragment
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val apiKey = API.KEY
    private val client = OkHttpClient()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var database: AppDatabase
    private lateinit var viewModel: SearchViewModel
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        database = AppDatabase.getDatabase(context)
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
        loadLastRequests()

        val fromAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line)
        val toAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line)

        setupAutoComplete(binding.etFrom, fromAdapter)
        setupAutoComplete(binding.etTo, toAdapter)

        binding.icReverse.setOnClickListener { onReverse() }

        binding.btnDate.setOnClickListener { showDatePicker() }

        viewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)

        observeViewModel()

        binding.btnSearch.setOnClickListener { onSearchClick() }

        // Инициализация RecyclerView
        recyclerView = binding.rcSchedule
        scheduleAdapter = ScheduleAdapter()
        recyclerView.adapter = scheduleAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupAutoComplete(field: AutoCompleteTextView, adapter: ArrayAdapter<String>) {
        field.setAdapter(adapter)
        field.addTextChangedListener { text ->
            text?.takeIf { it.isNotEmpty() }?.let {
                fetchSuggestions(it.toString()) { suggestions ->
                    requireActivity().runOnUiThread {
                        adapter.clear()
                        adapter.addAll(suggestions)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun showDatePicker() {

        val dialog = CalendarDialogFragment { selectedDate ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.btnDate.text = dateFormat.format(selectedDate)
        }
        dialog.show(parentFragmentManager, "CalendarDialog")
    }

    private fun observeViewModel() {
        viewModel.schedule.observe(viewLifecycleOwner) { segments ->
            if (segments.isEmpty()) {
                binding.tvResult.text = "Ничего не найдено"
            } else {
                scheduleAdapter.setSegments(segments) // Используйте адаптер для отображения данных
                binding.tvResult.text = ""
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            binding.tvResult.text = "Ошибка: $errorMessage"
        }
    }

    private fun onReverse() {
        val from = binding.etFrom.text
        val to = binding.etTo.text
        binding.etFrom.text = to
        binding.etTo.text = from
    }

    private fun onSearchClick() {
        val cityFrom = binding.etFrom.text.toString().trim()
        val cityTo = binding.etTo.text.toString().trim()
        val selectedDate = binding.btnDate.text.toString()

        if (cityFrom.isEmpty() || cityTo.isEmpty()) {
            binding.tvResult.text = "Введите названия станций"
            return
        }

        fetchCityCode(cityFrom) { fromCode ->
            fetchCityCode(cityTo) { toCode ->
                if (fromCode != null && toCode != null) {
                    viewModel.fetchSchedule(apiKey, fromCode, toCode, selectedDate)
                    cacheRequest(cityFrom, cityTo)
                } else {
                    binding.tvResult.text = "Код города не найден"
                }
            }
        }
    }

    private fun fetchCityCode(query: String, callback: (String?) -> Unit) {
        performRequest("https://suggests.rasp.yandex.net/all_suggests?format=old&part=$query") { responseData ->
            responseData?.let {
                val jsonArray = JSONArray(it).getJSONArray(1)
                val firstItem = jsonArray.optJSONArray(0)
                callback(firstItem?.getString(0))
            } ?: callback(null)
        }
    }

    private fun fetchSuggestions(query: String, callback: (List<String>) -> Unit) {
        performRequest("https://suggests.rasp.yandex.net/all_suggests?format=old&part=$query") { responseData ->
            responseData?.let {
                val jsonArray = JSONArray(it).getJSONArray(1)
                val firstItem = jsonArray.optJSONArray(0)
                val suggestion = firstItem?.getString(1)
                callback(if (suggestion != null) listOf(suggestion) else emptyList())
            } ?: callback(emptyList())
        }
    }

    private fun performRequest(url: String, callback: (String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        callback(response.body?.string())
                    } else {
                        callback(null)
                    }
                }
            } catch (e: Exception) {
                callback(null)
            }
        }
    }

    private fun cacheRequest(cityFrom: String, cityTo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = database.searchRequestDao()
            dao.insertRequest(SearchRequest(cityFrom = cityFrom, cityTo = cityTo))
            dao.deleteOldRequests()
        }
    }

    private fun loadLastRequests() {
        CoroutineScope(Dispatchers.IO).launch {
            val lastRequests = database.searchRequestDao().getLastThreeRequests()
            requireActivity().runOnUiThread {
                // Отобразите результаты в UI
                val resultText = lastRequests.joinToString("\n") {
                    "${it.cityFrom}  —  ${it.cityTo}"
                }
                binding.tvLastRequests.text = resultText
            }
        }
    }

}
