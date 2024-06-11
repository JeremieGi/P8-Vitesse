package com.openclassrooms.p8vitesse.ui.list

import com.openclassrooms.p8vitesse.domain.model.Candidate

/**
 * Class for the communication between the ViewModel and the fragment
 */
data class CandidateListUIStates (

    val listCandidates : List<Candidate> = emptyList(),

    val isLoading: Boolean = false,

    val errorMessage: String? = null

)
