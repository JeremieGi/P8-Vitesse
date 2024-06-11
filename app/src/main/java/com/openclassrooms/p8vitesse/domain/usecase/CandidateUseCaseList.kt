package com.openclassrooms.p8vitesse.domain.usecase

import com.openclassrooms.p8vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8vitesse.data.repository.ResultDatabase
import com.openclassrooms.p8vitesse.domain.model.Candidate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CandidateUseCaseList @Inject constructor(
    private val candidateRepository: CandidateRepository
) {

    /**
     * Emit a flow of result
     * @param bFavorite : 0 = no favorite, 1 favorite, null all
     * @param sFilterName : null = no filter else a string to search candidate by name
     */
    fun execute(bFavorite: Boolean?, sFilterName : String?): Flow<ResultDatabase<List<Candidate>>> {

        return candidateRepository.getListCandidate(bFavorite,sFilterName)

    }


}