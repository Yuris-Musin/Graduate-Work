package ru.musindev.graduate_work.views.adapters

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.musindev.graduate_work.R
import ru.musindev.graduate_work.databinding.ItemScheduleBinding
import ru.musindev.graduate_work.domain.models.Segment

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private var segments: List<Segment> = emptyList()

    fun setSegments(newSegments: List<Segment>) {
        segments = newSegments
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(segments[position])
    }

    override fun getItemCount(): Int = segments.size

    class ScheduleViewHolder(private val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(segment: Segment) {
            binding.tvDeparture.text = "Отправление: ${segment.departure}"
            binding.tvArrival.text = "Прибытие: ${segment.arrival}"
            binding.tvRoute.text = "Маршрут: ${segment.thread.title}"

            binding.ivAddToCalendar.setOnClickListener {
                binding.ivAddToCalendar.setImageResource(R.drawable.baseline_favorite_24)
                addEventToCalendar(it.context, segment)
            }
        }

        private fun addEventToCalendar(context: Context, segment: Segment) {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, "Поездка: ${segment.thread.title}")
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, segment.departureTimeMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, segment.arrivalTimeMillis)
                putExtra(CalendarContract.Events.EVENT_LOCATION, segment.departure + " - " + segment.arrival)
            }
            context.startActivity(intent)
        }
    }
}
