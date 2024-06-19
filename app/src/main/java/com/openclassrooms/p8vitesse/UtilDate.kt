package com.openclassrooms.p8vitesse

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Transforme une date en chaine au format local
 */
fun sLocalDateToString(dDate : Date): String {
    val locale = Locale.getDefault()
    val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, locale)
    return dateFormat.format(dDate)
}


/**
 * Transforme une chaine au format local en date
 */
fun dStringToLocalDate(sDate : String) : Date? {
    val locale = Locale.getDefault()
    val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, locale)
    return try {
        dateFormat.parse(sDate)
    } catch (e: Exception) {
        null
    }
}