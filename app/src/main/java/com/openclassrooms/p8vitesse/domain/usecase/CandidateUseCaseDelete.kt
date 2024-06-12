package com.openclassrooms.p8vitesse.domain.usecase

import com.openclassrooms.p8vitesse.data.repository.CandidateRepository
import javax.inject.Inject

class CandidateUseCaseDelete @Inject constructor(
    private val candidateRepository: CandidateRepository
) {

    suspend fun execute(id : Long) {
        candidateRepository.deleteCandidate(id)
    }

}