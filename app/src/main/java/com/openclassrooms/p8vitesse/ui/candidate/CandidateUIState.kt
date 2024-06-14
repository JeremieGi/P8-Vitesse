package com.openclassrooms.p8vitesse.ui.candidate

import com.openclassrooms.p8vitesse.domain.model.Candidate

sealed class CandidateUIState {

    data class Success(val candidate: Candidate) : CandidateUIState()
    data class Error(val exception: Throwable) : CandidateUIState()
    data object OperationDeleteCompleted : CandidateUIState()
    data object OperationAddCompleted : CandidateUIState()

    data class Conversion(val resultConversion : String)  : CandidateUIState()


}