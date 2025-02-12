package ru.musindev.graduate_work.views.adapters

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.musindev.graduate_work.R
import ru.musindev.graduate_work.databinding.ItemMonthBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class MonthAdapter(
    private val context: Context,
    private val startDate: Calendar,
    private val monthsToShow: Int,
    private val onDateSelected: (Date) -> Unit
) : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    private var selectedDate: Calendar? = null  // Храним выбранную дату в Calendar для сравнения
    private var todayDate: Calendar = Calendar.getInstance()  // Для текущей даты

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val binding = ItemMonthBinding.inflate(LayoutInflater.from(context), parent, false)
        return MonthViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = startDate.time
        calendar.add(Calendar.MONTH, position)

        holder.bind(calendar, selectedDate, todayDate) { date ->
            // Сбросить подсветку текущей даты, если она была подсвечена
            val oldSelectedDate = selectedDate?.clone() as? Calendar

            selectedDate = Calendar.getInstance().apply { time = date }

            // Обновляем только те элементы, которые изменились
            oldSelectedDate?.let { notifyItemChanged(getMonthPosition(it)) }
            notifyItemChanged(getMonthPosition(selectedDate!!))

            onDateSelected(date)
        }
    }

    override fun getItemCount(): Int = monthsToShow

    private fun getMonthPosition(calendar: Calendar): Int {
        val diffYear = calendar.get(Calendar.YEAR) - startDate.get(Calendar.YEAR)
        val diffMonth = calendar.get(Calendar.MONTH) - startDate.get(Calendar.MONTH)
        return diffYear * 12 + diffMonth
    }

    class MonthViewHolder(private val binding: ItemMonthBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            calendar: Calendar,
            selectedDate: Calendar?,
            todayDate: Calendar,
            onDateSelected: (Date) -> Unit
        ) {
            val formatter = DateTimeFormatter.ofPattern("LLLL", Locale("ru"))
            val month = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1)
                .format(formatter)
                .replaceFirstChar { it.uppercaseChar() }

            binding.tvMonthTitle.text = month
            binding.gridDays.removeAllViews()

            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2

            for (i in 0 until firstDayOfWeek) {
                binding.gridDays.addView(createEmptyDay())
            }

            for (day in 1..daysInMonth) {
                val dayView = createDayView(day, calendar, selectedDate, todayDate) { selectedDay ->
                    val date = Calendar.getInstance().apply {
                        set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), selectedDay)
                    }.time
                    onDateSelected(date)
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

        private fun createDayView(
            day: Int,
            calendar: Calendar,
            selectedDate: Calendar?,
            todayDate: Calendar,
            onClick: (Int) -> Unit
        ): TextView {
            val context = binding.root.context
            val date = Calendar.getInstance().apply {
                set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), day)
            }

            return TextView(context).apply {
                text = day.toString()
                textSize = 16f
                gravity = android.view.Gravity.CENTER
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
                setPadding(8, 8, 8, 8)

                val isPastDate = date.before(todayDate)
                isEnabled = !isPastDate
                alpha = if (isPastDate) 0.4f else 1f

                if (!isPastDate) {
                    setOnClickListener { onClick(day) }
                }

                // Подсветка текущей даты
                if (date.get(Calendar.YEAR) == todayDate.get(Calendar.YEAR) &&
                    date.get(Calendar.MONTH) == todayDate.get(Calendar.MONTH) &&
                    date.get(Calendar.DAY_OF_MONTH) == todayDate.get(Calendar.DAY_OF_MONTH)
                ) {
                    // Сбрасываем фон, если выбрана другая дата
                    if ((selectedDate == null && date != selectedDate) || date == selectedDate) {
                        setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                        setTextColor(Color.BLACK)
                    } else {
                        setBackgroundColor(Color.TRANSPARENT)
                    }
                }
                // Подсветка выбранной даты
                else if (selectedDate != null &&
                    date.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                    date.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                    date.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH)
                ) {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                    setTextColor(Color.BLACK)
                } else {
                    setBackgroundColor(Color.TRANSPARENT) // Сбрасываем фон, если дата не выбрана
                }
            }
        }
    }
}
