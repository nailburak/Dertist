package com.morhpt.dertist.help

import android.util.Log

/**
 * Created by nbura on 2017-11-17.
 */
class TimeMinute (val time: Long) {
    private val hours   = time / 60
    private val days    = time / 1440
    private val weeks   = time / 10080
    private val months  = time / 43200
    private val years   = time / 525600

    data class Time(val time: Long, val type: String)

    fun calculate(): HashMap<String, Time> {
        val data = HashMap<String, Time>()
        when {
            hours >= 1 && days < 1 -> data.put("s", Time(hours, "hours"))
            days >= 1 && weeks < 1 -> data.put("s", Time(days, "days"))
            weeks >= 1 && months < 1 -> data.put("s", Time(weeks, "weeks"))
            months >= 1 && years < 1 -> data.put("s", Time(months, "months"))
            years <= 1 -> data.put("s", Time(years, "years"))
            else -> data.put("s", Time(time, "minutes"))
        }
        return data
    }
}