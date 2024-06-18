package com.openclassrooms.p8vitesse.data.repository

import android.util.Log
import com.openclassrooms.p8vitesse.TAG_DEBUG
import com.openclassrooms.p8vitesse.data.dao.CandidateDao
import com.openclassrooms.p8vitesse.domain.model.Candidate
import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class CandidateRepository (
    private val candidateDao : CandidateDao
){


    // C'est dans le ViewModel dédié au favori que je filtre par favoris
    // J'ai hésité à faire 2 flows mais çà complexifie grandement le code
    private val _allCandidatesFlow = MutableSharedFlow<ResultCustom<List<Candidate>>>()
    val allCandidatesFlow: SharedFlow<ResultCustom<List<Candidate>>> get() = _allCandidatesFlow


    suspend fun getListAllCandidates(sFilterName: String?) {

        withContext(Dispatchers.IO) {
            flow {

                Log.d(TAG_DEBUG,"  getListAllCandidate(sFilterName = $sFilterName )")

                // T003 - Loading state
                emit(ResultCustom.Loading)

                //delay(5*1000) // To test T003 - Loading state

                // Pas d'utilisation du filtre Favori
                val flowListCandidates = candidateDao.getCandidates(bFavoriteP = null, sFilterName = sFilterName)

                // transform in model object
                val resultListCandidate = flowListCandidates
                    .first()
                    .map {
                        it.toModelCandidate()
                    }

                // emit List<Candidate> in success
                emit(ResultCustom.Success(resultListCandidate))

            }.catch { error ->
                Log.d(TAG_DEBUG,"Catch Exception dans getListAllCandidate(  $sFilterName )")
                emit(ResultCustom.Failure(error.message))
            }.collect { result ->
                // On émet le flow généré dans le flow du repository
                _allCandidatesFlow.emit(result)
            }
        }


    }


    suspend fun getCandidate(lID: Long): Result<Candidate> {

        return try {
            val candidateDto = candidateDao.getCandidateById(lID).first()
            val candidateModel = candidateDto.toModelCandidate()
            Result.success(candidateModel)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    suspend fun addCandidate(candidate: Candidate) : Long {
        return candidateDao.insertCandidate(candidate.toDto())
    }

    suspend fun deleteCandidate(candidate : Candidate) {

        candidateDao.deleteCandidateById(candidate.id?:0)

        // TODO prio : Doit-on gérer les fichiers images orphelins
        // Fait ici mais çà va être compliqué pour les updates
        if (candidate.photoFilePath.isNotEmpty()){
            deleteFile(candidate.photoFilePath)
        }

    }

    private fun deleteFile(photoFilePath: String) {

        val file = File(photoFilePath)
        try {
            if (file.exists())
                file.delete()

        } catch (e: IOException) {
            Log.d(TAG_DEBUG,"deleteFile $photoFilePath - An error occurred: ${e.message}")
        }
    }

    suspend fun updateCandidate(candidate: Candidate) : Int {
        return candidateDao.updateCandidate(candidate.toDto())
    }

    suspend fun updateCandidate(id : Long, bFavorite : Boolean) : Int {
        return candidateDao.updateCandidateTopFavorite(id,bFavorite)
    }



}