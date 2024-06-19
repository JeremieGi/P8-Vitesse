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

        // Je n'ai pas trouvé une solution plus simple
        // J'ai besoin du chemin de ce répertoire dans la classe Candidate surtout quand elle est appelée depuis le Repository pour gérer les fichiers images
        Candidate.fCurrentRep = applicationContext.filesDir

        // Create the database if not existing
        AppDataBase.getDatabase(this, CoroutineScope(Dispatchers.Default))

    }

}