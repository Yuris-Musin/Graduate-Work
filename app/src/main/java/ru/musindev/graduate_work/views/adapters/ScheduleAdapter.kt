package ru.musindev.graduate_work.views.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import ru.musindev.graduate_work.databinding.ItemScheduleBinding
import ru.musindev.graduate_work.domain.models.Segment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(segments[position])
    }

    override fun getItemCount(): Int = segments.size

    class ScheduleViewHolder(private val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(segment: Segment) {
            val departureWords = segment.departure.split("T")

            val dateDeparture = LocalDate.parse(departureWords[0], DateTimeFormatter.ISO_DATE)
            val formatterDeparture = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
            binding.dateDeparture.text = dateDeparture.format(formatterDeparture)

            val departureTime = departureWords[1].split(":").take(2).joinToString(":")


            val arrivalWords = segment.arrival.split("T")

            val date = LocalDate.parse(arrivalWords[0], DateTimeFormatter.ISO_DATE)
            val formatterArrival = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
            binding.dateArrival.text = date.format(formatterArrival)

            val arrivalTime = arrivalWords[1].split(":").take(2).joinToString(":")
            binding.tvDeparture.text = departureTime
            binding.tvArrival.text = arrivalTime
            binding.tvRoute.text = "Маршрут: ${segment.thread.title}"

//            binding.ivAddToCalendar.setOnClickListener {
//                binding.ivAddToCalendar.setImageResource(R.drawable.baseline_favorite_24)
//                addEventToCalendar(it.context, segment)
//            }
        }

//        private fun addEventToCalendar(context: Context, segment: Segment) {
//            val intent = Intent(Intent.ACTION_INSERT).apply {
//                data = CalendarContract.Events.CONTENT_URI
//                putExtra(CalendarContract.Events.TITLE, "Поездка: ${segment.thread.title}")
//                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, segment.departureTimeMillis)
//                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, segment.arrivalTimeMillis)
//                putExtra(CalendarContract.Events.EVENT_LOCATION, segment.departure + " - " + segment.arrival)
//            }
//            context.startActivity(intent)
//        }
    }
}
