package com.openclassrooms.p8vitesse.domain.usecase

import com.openclassrooms.p8vitesse.data.repository.CandidateRepository
import javax.inject.Inject

class CandidateUseCaseSetFavorite @Inject constructor(
    private val candidateRepository: CandidateRepository
) {

    suspend fun execute(id: Long, bNewFavoriteStatut: Boolean) : Int {
        return candidateRepository.updateCandidateTopFavorite(id,bNewFavoriteStatut)
    }

}