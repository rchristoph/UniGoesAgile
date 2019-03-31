package com.Info_DH.sgru_rchr.UniversityGoesAgile
import java.text.SimpleDateFormat
import java.util.*

object CalendarHelper {

    // Gib das aktuelle Datum als Long aus
    fun getCurrentDateInMills() : Long{
        var timeFormat = Calendar.getInstance().timeInMillis
        val format = SimpleDateFormat("yyyy.MM.dd")
        val timeFormatformat = format.format(timeFormat)
        println("Das ist mein TimeFormat: $timeFormatformat")
        return Calendar.getInstance().timeInMillis
    }

    //Mache aus einem String den Datentyp long
    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("dd/MM/yyyy")
        return df.parse(date).time
    }

}