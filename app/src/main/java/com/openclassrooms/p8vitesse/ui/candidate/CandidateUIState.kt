package com.openclassrooms.p8vitesse.ui.candidate

import com.openclassrooms.p8vitesse.domain.model.Candidate

// TODO : J'utilise cette classe entre 2 fragments / ViewsModel différents ? est ce que çà se fait ?
// J'utilise aussi cette classe pour récupérer OperationAddCompleted/OperationUpdatedCompleted... => dois-je faire des classes à part ?

sealed class CandidateUIState {

    data class Success(val candidate: Candidate) : CandidateUIState()
    data class Error(val exception: Throwable) : CandidateUIState()
    data object OperationDeleteCompleted : CandidateUIState()
    data object OperationAddCompleted : CandidateUIState()
    data object OperationUpdatedCompleted : CandidateUIState()
    data object OperationFavoriteUpdated : CandidateUIState()

    data class Conversion(val resultConversion : String)  : CandidateUIState()


}