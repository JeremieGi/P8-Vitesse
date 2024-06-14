package com.openclassrooms.p8vitesse.ui.candidate

import com.openclassrooms.p8vitesse.domain.model.Candidate

sealed class CandidateState {

    data class Success(val candidate: Candidate) : CandidateState()
    data class Error(val exception: Throwable) : CandidateState()
    object OperationDeleteCompleted : CandidateState()


}