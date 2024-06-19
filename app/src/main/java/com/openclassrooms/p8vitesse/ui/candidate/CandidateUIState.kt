package com.openclassrooms.p8vitesse.ui.candidate

import com.openclassrooms.p8vitesse.domain.model.Candidate

// Classe qui permet la communication entre les fragments Edit / Display et leurs ViewModels

sealed class CandidateUIState {

    data class Success(val candidate: Candidate) : CandidateUIState()
    data class Error(val exception: Throwable) : CandidateUIState()
    data object OperationDeleteCompleted : CandidateUIState()
    data object OperationAddCompleted : CandidateUIState()
    data object OperationUpdatedCompleted : CandidateUIState()
    data object OperationFavoriteUpdated : CandidateUIState()

    data class Conversion(val resultConversion : String)  : CandidateUIState()


}