package com.openclassrooms.p8vitesse.domain.usecase

import com.openclassrooms.p8vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8vitesse.domain.model.Candidate
import javax.inject.Inject



class CandidateUseCaseUpdate @Inject constructor(
    private val candidateRepository: CandidateRepository
) {

    suspend fun execute(candidate: Candidate) : Int {
        return candidateRepository.updateCandidate(candidate)
    }

    // TODO : Faire un UseCase
    suspend fun setFavorite(id: Long, bNewFavoriteStatut: Boolean) : Int {
        return candidateRepository.updateCandidate(id,bNewFavoriteStatut)
    }

}

