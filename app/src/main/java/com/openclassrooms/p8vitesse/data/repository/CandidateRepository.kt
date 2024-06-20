package com.openclassrooms.p8vitesse.data.repository

import android.util.Log
import com.openclassrooms.p8vitesse.TAG_DEBUG
import com.openclassrooms.p8vitesse.data.dao.CandidateDao
import com.openclassrooms.p8vitesse.domain.model.Candidate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class CandidateRepository (
    private val candidateDao : CandidateDao
){


    // C'est dans le ViewModel dédié au favori que je filtre par favoris
    // J'ai hésité à faire 2 flows mais çà complexifie grandement le code
    private val _allCandidatesFlow = MutableSharedFlow<ResultCustom<List<Candidate>>>()
    val allCandidatesFlow: SharedFlow<ResultCustom<List<Candidate>>> get() = _allCandidatesFlow


    /**
     * List the candidate filter by name
     * @param sFilterName : Filter on the first name and last name. Empty string = no filter
     */
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

        val id = candidateDao.insertCandidate(candidate.toDto())

        // Si une photo a été renseignée
        if (candidate.sPathTempSelectedPhoto.isNotEmpty()){
            candidate.transferAndFormatPhotoFile(id)
        }


        return id

    }

    suspend fun deleteCandidate(candidate : Candidate) {

        candidateDao.deleteCandidateById(candidate.id?:0)

        candidate.deletePhotoFile()

    }



    suspend fun updateCandidate(candidate: Candidate) : Int {

        if (candidate.id != null){
            // Si une photo a été renseignée
            if (candidate.sPathTempSelectedPhoto.isNotEmpty()){
                candidate.transferAndFormatPhotoFile()
            }
        }
        return candidateDao.updateCandidate(candidate.toDto())
    }

    suspend fun updateCandidateTopFavorite(id : Long, bFavorite : Boolean) : Int {

        return candidateDao.updateCandidateTopFavorite(id,bFavorite)

    }



}