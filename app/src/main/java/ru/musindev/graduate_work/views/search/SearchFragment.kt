package ru.musindev.graduate_work.views.search

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
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

        // Обработка клика по кнопке поиска
        binding.btnSearch.setOnClickListener {
            val from = binding.etFrom.text.toString().trim()
            val to = binding.etTo.text.toString().trim()

            var selectDate = ""

            selectDate = if(binding.btnDate.text.isEmpty()) {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            } else binding.btnDate.text.toString()

            if (from.isEmpty() || to.isEmpty()) {
                binding.tvResult.text = "Введите названия станций"
            } else {
                viewModel.fetchSchedule(apiKey, from, to, selectDate)
            }
        }
    }

}