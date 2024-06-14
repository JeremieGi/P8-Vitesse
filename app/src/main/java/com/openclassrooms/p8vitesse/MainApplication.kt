package com.openclassrooms.p8vitesse

import android.app.Application
import com.openclassrooms.p8vitesse.data.dao.AppDataBase
import com.openclassrooms.p8vitesse.data.network.ICurrencyAPI
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


    override fun onCreate() {

        super.onCreate()

        // Create the database if not existing
        AppDataBase.getDatabase(this, CoroutineScope(Dispatchers.Default))

    }

}