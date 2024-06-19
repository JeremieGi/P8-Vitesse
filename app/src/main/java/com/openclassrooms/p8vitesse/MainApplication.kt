package com.openclassrooms.p8vitesse

import android.app.Application
import com.openclassrooms.p8vitesse.data.dao.AppDataBase
import com.openclassrooms.p8vitesse.domain.model.Candidate
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


@HiltAndroidApp
class MainApplication  : Application(){


    override fun onCreate() {

        super.onCreate()

        Candidate.fCurrentRep = applicationContext.filesDir
        //Candidate.fCurrentRep = applicationContext.cacheDir

        // Create the database if not existing
        AppDataBase.getDatabase(this, CoroutineScope(Dispatchers.Default))

    }

}