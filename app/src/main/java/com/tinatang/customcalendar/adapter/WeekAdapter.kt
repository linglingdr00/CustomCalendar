package com.tinatang.customcalendar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinatang.customcalendar.databinding.WeekItemBinding

class WeekAdapter(private val daysOfWeek: List<String>) :
    RecyclerView.Adapter<WeekAdapter.WeekViewHolder>() {

    inner class WeekViewHolder(
        private val binding: WeekItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(day: String) {
            binding.weekTextView.text = day
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val binding = WeekItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeekViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val day = daysOfWeek[position]
        holder.bind(day)
    }

    override fun getItemCount(): Int = daysOfWeek.size
}