package ru.musindev.graduate_work.views.calendar

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import ru.musindev.graduate_work.databinding.DialogCalendarBinding
import ru.musindev.graduate_work.views.adapters.MonthAdapter
import java.util.*

class CalendarDialogFragment(
    private val onDateSelected: (Date) -> Unit // Callback для возврата выбранной даты
) : DialogFragment() {

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar) // Устанавливаем стиль на весь экран
        val binding = DialogCalendarBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        val currentMonth = Calendar.getInstance() // Текущий месяц
        val monthsToShow = 4 // Количество месяцев для отображения

        val adapter = MonthAdapter(
            context = requireContext(),
            startDate = currentMonth,
            monthsToShow = monthsToShow
        ) { selectedDate ->
            onDateSelected(selectedDate) // Возвращаем выбранную дату в вызывающий фрагмент
            dialog.dismiss() // Закрываем диалог
        }

        // Настройка RecyclerView для отображения месяцев
        binding.recyclerCalendar.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCalendar.adapter = adapter

        return dialog
    }

    override fun onStart() {
        super.onStart()
        // Настройка полноэкранного режима
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
    }
}
