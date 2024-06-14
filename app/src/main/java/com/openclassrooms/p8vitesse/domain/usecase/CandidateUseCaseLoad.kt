package com.openclassrooms.p8vitesse.domain.usecase

import com.openclassrooms.p8vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8vitesse.domain.model.Candidate
import javax.inject.Inject

class CandidateUseCaseLoad @Inject constructor(
    private val candidateRepository: CandidateRepository
) {

    suspend fun execute(lID : Long): Result<Candidate> {

        return candidateRepository.getCandidate(lID)

    }

}