package com.openclassrooms.p8vitesse.domain.usecase

import com.openclassrooms.p8vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8vitesse.data.repository.ResultCustom
import com.openclassrooms.p8vitesse.domain.model.Candidate
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class CandidateUseCaseList @Inject constructor(
    private val candidateRepository: CandidateRepository
) {
/*
    /**
     * Emit a flow of result
     * @param bFavorite : 0 = no favorite, 1 favorite, null all
     * @param sFilterName : null = no filter else a string to search candidate by name
     */
    fun execute(bFavorite: Boolean?, sFilterName : String?): Flow<ResultDatabase<List<Candidate>>> {

        return candidateRepository.getListCandidate(bFavorite,sFilterName)

    }
*/

    // Accesseur au flow partagé
    val allCandidatesFlow: SharedFlow<ResultCustom<List<Candidate>>> get() = candidateRepository.allCandidatesFlow

//    // Mise à jour de la recherche
//    suspend fun execute(bFavorite: Boolean?, sFilterName : String?) {
//
//        return candidateRepository.getListAllCandidates(bFavorite,sFilterName)
//
//    }

    suspend fun execute(sFilterName : String?) {

        return candidateRepository.getListAllCandidates(sFilterName)

    }


}