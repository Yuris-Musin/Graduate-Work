package ru.musindev.graduate_work.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
            binding.tvDeparture.text = "Отправление: " + segment.departure
            binding.tvArrival.text = "Прибытие: " + segment.arrival
            binding.tvRoute.text = "Маршрут: " + segment.thread.title
        }
    }
}
