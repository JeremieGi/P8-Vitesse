package com.openclassrooms.p8vitesse.data.repository

import com.openclassrooms.p8vitesse.data.dao.CandidateDao
import com.openclassrooms.p8vitesse.domain.model.Candidate
//import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class CandidateRepository (
    private val candidateDao : CandidateDao
){
    /**
     * Emit a flow of result
     * @param bFavoriteP : 0 = no favorite, 1 favorite, null all
     * @param sFilterName : null = no filter else a string to search candidate by name
     */
    fun getListCandidate(bFavorite: Boolean?, sFilterName : String?) : Flow<ResultDatabase<List<Candidate>>> = flow {

        // T003 - Loading state
        emit(ResultDatabase.Loading)

        //delay(5*1000) // To test T003 - Loading state

        // TODO : J'ai pris le parti de descendre la recherche jusqu'à la base de données
        //  (plus maintenable / plus compréhensible / plus testable)

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
        // emit an error
        emit(ResultDatabase.Failure(error.message+" "+error.cause?.message)) // Message enrichi
    }


}