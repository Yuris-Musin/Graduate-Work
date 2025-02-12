package ru.musindev.graduate_work.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.musindev.graduate_work.databinding.ItemMonthBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MonthAdapter(
    private val context: Context,
    private val startDate: Calendar, // Текущая дата
    private val monthsToShow: Int,   // Количество месяцев
    private val onDateSelected: (Date) -> Unit
) : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val binding = ItemMonthBinding.inflate(LayoutInflater.from(context), parent, false)
        return MonthViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = startDate.time
        calendar.add(Calendar.MONTH, position)

        holder.bind(calendar, onDateSelected)
    }

    override fun getItemCount(): Int = monthsToShow

    class MonthViewHolder(private val binding: ItemMonthBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(calendar: Calendar, onDateSelected: (Date) -> Unit) {
            val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            binding.tvMonthTitle.text = monthFormat.format(calendar.time)

            // Очистка сетки перед заполнением
            binding.gridDays.removeAllViews()

            // Установка дней недели
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2

            // Добавление пустых ячеек для выравнивания
            for (i in 0 until firstDayOfWeek) {
                binding.gridDays.addView(createEmptyDay())
            }

            // Добавление дней месяца
            for (day in 1..daysInMonth) {
                val dayView = createDayView(day, calendar) { selectedDay ->
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                    onDateSelected(calendar.time) // Возврат выбранной даты
                }
                binding.gridDays.addView(dayView)
            }
        }

        private fun createEmptyDay(): View {
            return View(binding.root.context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
            }
        }

        private fun createDayView(day: Int, calendar: Calendar, onClick: (Int) -> Unit): TextView {
            val currentDay = Calendar.getInstance()
            return TextView(binding.root.context).apply {
                text = day.toString()
                textSize = 16f
                gravity = android.view.Gravity.CENTER
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
                setPadding(8, 8, 8, 8)

                // Делаем некликабельными даты до сегодняшнего дня
                val isPastDate = calendar.get(Calendar.YEAR) < currentDay.get(Calendar.YEAR) ||
                        (calendar.get(Calendar.YEAR) == currentDay.get(Calendar.YEAR) &&
                                calendar.get(Calendar.MONTH) == currentDay.get(Calendar.MONTH) &&
                                day < currentDay.get(Calendar.DAY_OF_MONTH))

                isEnabled = !isPastDate
                alpha = if (isPastDate) 0.4f else 1f

                if (!isPastDate) {
                    setOnClickListener { onClick(day) }
                }
            }
        }
    }
}

