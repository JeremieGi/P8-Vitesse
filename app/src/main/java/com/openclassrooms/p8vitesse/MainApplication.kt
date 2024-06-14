package com.openclassrooms.p8vitesse

import android.app.Application
import com.openclassrooms.p8vitesse.data.dao.AppDataBase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@HiltAndroidApp
class MainApplication  : Application(){

    companion object{

        const val TAG_DEBUG = "**DEBUG**"

        // TODO : Voir si il y a un endroit plus pertinent ou placer ces fonctions
        fun sLocalDateToString(dDate : Date): String {
            val locale = Locale.getDefault()
            val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, locale)
            val formattedDate = dateFormat.format(dDate)
            return formattedDate
        }

        fun dStringToLocalDate(sDate : String) : Date? {
            val locale = Locale.getDefault()
            val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, locale)
            return try {
                dateFormat.parse(sDate)
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun onCreate() {

        super.onCreate()

        // Create the database if not existing
        AppDataBase.getDatabase(this, CoroutineScope(Dispatchers.Default))

    }

}