package com.openclassrooms.p8vitesse.ui.list

import com.openclassrooms.p8vitesse.domain.model.Candidate

data class CandidateListUIStates (

    val listCandidates : List<Candidate> = emptyList(),

    val isLoading: Boolean = false,

    val errorMessage: String? = null

)
