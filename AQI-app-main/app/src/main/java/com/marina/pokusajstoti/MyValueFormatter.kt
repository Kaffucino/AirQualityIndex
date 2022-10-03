package com.marina.pokusajstoti

import android.util.Log
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class MyValueFormatter(val offset : Float) : ValueFormatter() {

    private val days= mapOf( 0.0f to "Monday", 1.0f to "Tuesday", 2.0f to "Wednesday", 3.0f to "Thursday",4.0f to "Friday", 5.0f to "Satudray",6.0f to "Sunday")

    override fun getAxisLabel(value: Float, axis: AxisBase?): String? {
        var add = 7 - offset
        Log.i("OFFSET NEKI " , " " + value + add)
        return if(days.containsKey((value + add + offset)%7)) days[(value + add + offset)%7] else value.toString()
    }

}