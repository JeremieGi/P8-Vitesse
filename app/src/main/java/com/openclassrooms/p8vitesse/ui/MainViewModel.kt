package com.openclassrooms.p8vitesse.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.p8vitesse.domain.usecase.CandidateUseCaseList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCandidateUseCaseList : CandidateUseCaseList
) : ViewModel(){



    fun loadCandidates(sFilter : String, nCurrent : Int) {

        var bFavorite : Boolean ? = null
        if (nCurrent==1){
            bFavorite = true
        }
        getCandidateUseCaseList.execute(bFavorite,sFilter)

    }


}