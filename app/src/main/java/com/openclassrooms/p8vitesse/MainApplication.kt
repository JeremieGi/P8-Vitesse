package com.openclassrooms.p8vitesse

import android.app.Application
import com.openclassrooms.p8vitesse.data.dao.AppDataBase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@HiltAndroidApp
class MainApplication  : Application(){

    companion object{
        const val TAG_DEBUG = "**DEBUG**"
    }

    override fun onCreate() {

        super.onCreate()

        // Create the database if not existing
        AppDataBase.getDatabase(this, CoroutineScope(Dispatchers.Default))

    }

}