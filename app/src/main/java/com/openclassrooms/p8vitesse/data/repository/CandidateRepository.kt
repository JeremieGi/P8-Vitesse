package com.openclassrooms.p8vitesse.data.repository

import com.openclassrooms.p8vitesse.data.dao.CandidateDao
import com.openclassrooms.p8vitesse.data.entity.CandidateDto
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
     * @param nFavoriteP : -1 => all candidates, 0 => non-favorite candidates, 1 favorite candidates
     */
    fun getListCandidate(nFavoriteP: Int = -1): Flow<ResultDatabase<List<Candidate>>> = flow {

        // T003 - Loading state
        emit(ResultDatabase.Loading)

        //delay(5*1000) // To test T003 - Loading state

        // Do the adapted request
        val flowListCandidates : Flow<List<CandidateDto>>
        flowListCandidates = when (nFavoriteP) {
            0,1 -> candidateDao.getCandidatesFilterByFavorite(nFavoriteP)
            else -> candidateDao.getAllCandidates()
        }

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