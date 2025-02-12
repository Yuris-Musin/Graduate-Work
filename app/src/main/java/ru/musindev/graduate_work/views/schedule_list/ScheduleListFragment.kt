package ru.musindev.graduate_work.views.schedule_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.launch
import ru.musindev.graduate_work.API
import ru.musindev.graduate_work.databinding.FragmentScheduleListBinding
import ru.musindev.graduate_work.views.adapters.ScheduleAdapter
import ru.musindev.graduate_work.views.search.SearchViewModel
import javax.inject.Inject

class ScheduleListFragment : Fragment() {

    private var _binding: FragmentScheduleListBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SearchViewModel
    private lateinit var scheduleAdapter: ScheduleAdapter

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScheduleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация RecyclerView
        val recyclerView = binding.rcSchedule
        scheduleAdapter = ScheduleAdapter()
        recyclerView.adapter = scheduleAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val cityFrom = arguments?.getString("cityFrom").toString()
        val cityTo = arguments?.getString("cityTo").toString()
        val selectedDate = arguments?.getString("selectDate").toString()

        // Инициализация ViewModel
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[SearchViewModel::class.java]

        // Наблюдение за schedule
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.schedule.collect { segments ->
                if (segments.isEmpty()) {
                    binding.tvResult.text = "Маршруты не найдены"
                } else {
                    binding.tvResult.text = ""
                }
            }
        }

        // Наблюдение за error
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { errorMessage ->
                errorMessage?.let {
                    binding.tvResult.text = "Ошибка: $it"
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchSchedule(API.KEY, cityFrom, cityTo, selectedDate)
        }

        // Подписка на изменения данных
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.schedule.collect { segments ->
                scheduleAdapter.setSegments(segments)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect {
                binding.loadingLayout.root.isGone = !it
            }
        }
    }
}