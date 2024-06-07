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
     * @param nFavoriteP : -1 => all candidates, 0 => non-favorite candidates, 1 favorite candidates
     */
    fun execute(nFavoriteP : Int = -1): Flow<ResultDatabase<List<Candidate>>> {

        return candidateRepository.getListCandidate(nFavoriteP)

    }


}