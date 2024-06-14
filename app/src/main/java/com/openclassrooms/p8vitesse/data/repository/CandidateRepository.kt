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

class CandidateRepository (
    private val candidateDao : CandidateDao
){


    // C'est dans le ViewModel dédié au favori que je filtre par favoris
    // J'ai hésité à faire 2 flows mais çà complexifie grandement le code
    private val _allCandidatesFlow = MutableSharedFlow<ResultCustom<List<Candidate>>>()
    val allCandidatesFlow: SharedFlow<ResultCustom<List<Candidate>>> get() = _allCandidatesFlow



    /*


    /**
     * Emit in 2 flows
     * @param bFavoriteP : 0 = no favorite, 1 favorite, null all
     * @param sFilterName : null = no filter else a string to search candidate by name
     */
    suspend fun getListCandidate(bFavorite: Boolean?, sFilterName: String?) {

        withContext(Dispatchers.IO) {
            flow {

                Log.d(TAG_DEBUG,"  getListCandidate( bFavorite = $bFavorite, sFilterName = $sFilterName )")

                // T003 - Loading state
                emit(ResultDatabase.Loading)

                //delay(5*1000) // To test T003 - Loading state

                val flowListCandidates = candidateDao.getCandidates(bFavorite,sFilterName)

                // transform in model object
                val resultListCandidate = flowListCandidates
                    .first()
                    .map {
                        it.toModelCandidateList()
                    }

                // emit List<Candidate> in success
                emit(ResultDatabase.Success(resultListCandidate))

            }.catch { error ->
                Log.d(TAG_DEBUG,"Catch Exception dans getListCandidate( $bFavorite, $sFilterName )")
                emit(ResultDatabase.Failure(error.message+" "+error.cause?.message)) // Message enrichi
            }.collect { result ->

                // On émet le flow généré dans le flow du repository

                _candidatesFlow.emit(result)



            }
        }
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
                emit(ResultCustom.Failure(error.message+" "+error.cause?.message)) // Message enrichi
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

    suspend fun deleteCandidate(id : Long) {
        candidateDao.deleteCandidateById(id)
    }


}