package com.Info_DH.sgru_rchr.UniversityGoesAgile
import java.text.SimpleDateFormat
import java.util.*

object CalendarHelper {

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun getCurrentDateInMills() : Long{
        var timeFormat = Calendar.getInstance().timeInMillis
        val format = SimpleDateFormat("yyyy.MM.dd")
        val timeFormatformat = format.format(timeFormat)
        println("Das ist mein TimeFormat: $timeFormatformat")
        return Calendar.getInstance().timeInMillis
    }


    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("dd/MM/yyyy")
        return df.parse(date).time
    }

}