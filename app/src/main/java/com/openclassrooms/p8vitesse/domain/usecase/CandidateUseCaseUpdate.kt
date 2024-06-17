package com.openclassrooms.p8vitesse.domain.usecase

import com.openclassrooms.p8vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8vitesse.domain.model.Candidate
import javax.inject.Inject

// TODO : Ca fait beaucoup de UseCase tout çà ?

class CandidateUseCaseUpdate @Inject constructor(
    private val candidateRepository: CandidateRepository
) {

    suspend fun execute(candidate: Candidate) : Int {
        return candidateRepository.updateCandidate(candidate)
    }

    suspend fun setFavorite(id: Long, bNewFavoriteStatut: Boolean) : Int {
        return candidateRepository.updateCandidate(id,bNewFavoriteStatut)
    }

}

