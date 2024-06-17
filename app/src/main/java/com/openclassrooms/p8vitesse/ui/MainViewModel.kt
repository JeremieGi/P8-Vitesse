package com.openclassrooms.p8vitesse.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCandidateUseCaseList : CandidateUseCaseList
) : ViewModel(){

    var sFilter : String = ""


    /**
     * Launch the research
     * @param sFilter : Filter
     * @param nCurrent : Index of viewpager (0 or 1)
     */
    fun requestCandidates(sFilter : String) {

        this.sFilter = sFilter

        viewModelScope.launch {
            //getCandidateUseCaseList.execute(bFavorite, sFilter)
            getCandidateUseCaseList.execute(sFilter)
        }
    }
}