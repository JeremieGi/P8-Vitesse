package com.openclassrooms.p8vitesse.domain.usecase

import com.openclassrooms.p8vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8vitesse.domain.model.Candidate
import javax.inject.Inject

class CandidateUseCaseAdd @Inject constructor(
    private val candidateRepository: CandidateRepository
) {

    suspend fun execute(candidate: Candidate) : Long {
        return candidateRepository.addCandidate(candidate)
    }

}