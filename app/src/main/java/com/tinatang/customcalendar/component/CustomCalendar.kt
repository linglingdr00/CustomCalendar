package com.tinatang.customcalendar.component

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tinatang.customcalendar.R
import com.tinatang.customcalendar.adapter.CalendarAdapter
import com.tinatang.customcalendar.adapter.WeekAdapter
import com.tinatang.customcalendar.data.CalendarDay
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CustomCalendar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var weekRecyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var weekAdapter: WeekAdapter

    private lateinit var prevMonthButton: ImageButton
    private lateinit var nextMonthButton: ImageButton
    private lateinit var yearMonthTextView: TextView

    private val days: MutableList<CalendarDay> = mutableListOf()
    private var selectedDate: CalendarDay? = null
    private var currentYearMonth = LocalDate.now() // 月曆目前的年月

    enum class CalendarMode { NORMAL, FULL }

    companion object {
        private const val TAG = "CustomCalendar"
    }

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_custom_calendar, this, true)

        initializeViews()
        setupCalendarRecyclerView()
        setupWeekRecyclerView()
        setupButtonListeners()

        // 初始化日曆
        updateCalendar()
    }

    /** 初始化 UI 元件 */
    private fun initializeViews() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        weekRecyclerView = findViewById(R.id.weekRecyclerView)
        prevMonthButton = findViewById(R.id.prevMonthButton)
        nextMonthButton = findViewById(R.id.nextMonthButton)
        yearMonthTextView = findViewById(R.id.yearMonthTextView)
    }

    /** 設定 calendar recyclerView */
    private fun setupCalendarRecyclerView() {
        calendarRecyclerView.layoutManager = GridLayoutManager(context, 7)
        calendarAdapter = CalendarAdapter(days, ::onDateSelected, selectedDate)
        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun setupWeekRecyclerView() {
        // 可以動態傳入星期標題
        val weekList = listOf("日", "一", "二", "三", "四", "五", "六")
        weekRecyclerView.layoutManager = GridLayoutManager(context, 7)
        weekAdapter = WeekAdapter(weekList)
        weekRecyclerView.adapter = weekAdapter
    }

    /** 設定按鈕監聽器 */
    private fun setupButtonListeners() {
        // previous month button
        prevMonthButton.setOnClickListener { setPrevMonth(1) }
        // next month button
        nextMonthButton.setOnClickListener { setNextMonth(1) }
    }

    /** 設定月曆減一個月 */
    fun setPrevMonth(monthsToSub: Long) {
        currentYearMonth = currentYearMonth.minusMonths(monthsToSub).withDayOfMonth(1)
        updateCalendar()
    }

    /** 設定月曆加一個月 */
    fun setNextMonth(monthsToAdd: Long) {
        currentYearMonth = currentYearMonth.plusMonths(monthsToAdd).withDayOfMonth(1)
        updateCalendar()
    }

    /**
     * 依據 date 產生當月、上個月與下個月的日期清單 (days)，以填充 RecyclerView
     * 設定 當月的日期可點擊 (enable = true)，前後月的日期不可點擊 (enable = false)
     * 計算 上個月的空白天數 (prevSpace) 與 下個月的空白天數 (nextSpace)，確保畫面對齊
     * */
    private fun generateCalendarDays(date: LocalDate) {
        days.clear()
        val yearMonth = YearMonth.of(date.year, date.month)
        val daysInMonth = yearMonth.lengthOfMonth()

        val firstDayOfMonth = LocalDate.of(date.year, date.month, 1)
        val startIndex = firstDayOfMonth.dayOfWeek.value

        val prevSpace = startIndex - 1
        val nextSpace = 42 - prevSpace - daysInMonth - 1
        var position = 0

        val prevYearMonth = YearMonth.of(date.year, date.month - 1)
        val prevDaysInMonth = prevYearMonth.lengthOfMonth()
        val prevStartDay = prevDaysInMonth - prevSpace

        // 上個月
        for (day in prevStartDay..prevDaysInMonth) {
            days.add(CalendarDay(position++, LocalDate.of(date.year, date.month - 1, day), enable = false))
        }
        // 當月
        for (day in 1..daysInMonth) {
            days.add(CalendarDay(position++, LocalDate.of(date.year, date.month, day), enable = true))
        }
        // 下個月
        for (day in 1..nextSpace) {
            days.add(CalendarDay(position++, LocalDate.of(date.year, date.month + 1, day), enable = false))
        }
        // 通知 adapter 更新 UI
        calendarAdapter.notifyDataSetChanged()
    }

    /**
     * 處理 使用者點擊某個日期，將 selectedDate 設為該日期
     * 更新 adapter 讓 UI 反應變更
     */
    private fun onDateSelected(date: CalendarDay) {
        selectedDate = date
        Log.d(TAG, "selectedDate: $selectedDate")
        calendarAdapter.updateSelectedDate(selectedDate)
    }

    fun setSelectedDate(date: LocalDate) {
        // 檢查日期是否有效，找到該日期在 days 列表中的位置
        val position = findDayPosition(date)

        // 如果找到位置並且該日期在當月
        if (position != -1) {
            // 更新選中的日期
            selectedDate = CalendarDay(position, date, enable = true)
            // 更新日曆 UI
            updateCalendar()
            // 通知 adapter 更新 UI
            calendarAdapter.updateSelectedDate(selectedDate)
        } else {
            // 如果選擇的日期不在當月，切換到選擇的日期的月份
            currentYearMonth = LocalDate.of(date.year, date.month, date.dayOfMonth)
            // 更新選中的日期
            selectedDate = CalendarDay(position, date, enable = true)
            // 更新日曆 UI，顯示切換後的月份和選中的日期
            updateCalendar()
            // 通知 adapter 更新 UI
            calendarAdapter.updateSelectedDate(selectedDate)
        }
    }

    private fun findDayPosition(date: LocalDate): Int {
        return days.indexOfFirst { it.date == date }
    }

    /**
     * 主要負責 更新 UI（變更當前月份標題 & 重新產生日曆）
     * 每當 切換月份 或 初始化時 呼叫
     * */
    private fun updateCalendar() {
        val formatter = DateTimeFormatter.ofPattern("yyyy年M月")
        val yearMonthText = currentYearMonth.format(formatter)
        yearMonthTextView.text = yearMonthText
        generateCalendarDays(currentYearMonth)
    }
}