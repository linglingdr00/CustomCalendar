package com.tinatang.customcalendar.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinatang.customcalendar.R
import com.tinatang.customcalendar.data.CalendarDay
import com.tinatang.customcalendar.databinding.CalendarDayItemBinding

class CalendarAdapter(
    private val days: List<CalendarDay>,
    private val onDayClick: (CalendarDay) -> Unit,
    private var selectedDate: CalendarDay?
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    companion object {
        private const val TAG = "CalendarAdapter"
    }

    inner class DayViewHolder(private val binding: CalendarDayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: CalendarDay, selectedDate: CalendarDay?) {
            binding.dayTextView.text = date.date.dayOfMonth.toString()

            // 使用 selectedDate 來判斷是否選中
            val isSelected = selectedDate?.date == date.date
            binding.dayTextView.isSelected = isSelected

            // 設定選取時的背景
            if (isSelected) {
                binding.root.setBackgroundResource(R.drawable.day_background_selected)
            } else {
                binding.root.setBackgroundColor(Color.TRANSPARENT)
            }

            // 只有可點選的日期才能點擊
            if (date.enable) {
                binding.root.setOnClickListener {
                    onDayClick(date)
                }
            } else {
                // 不可點選的日期文字顏色
                binding.dayTextView.setTextColor(Color.TRANSPARENT)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = CalendarDayItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.bind(day, selectedDate)
    }

    override fun getItemCount(): Int = days.size

    fun updateSelectedDate(date: CalendarDay?) {
        selectedDate = date
        notifyDataSetChanged()
    }
}