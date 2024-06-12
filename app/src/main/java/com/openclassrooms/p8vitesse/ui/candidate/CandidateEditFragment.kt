package com.openclassrooms.p8vitesse.ui.candidate

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.openclassrooms.p8vitesse.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CandidateEditFragment : Fragment() {

    companion object {
        fun newInstance() = CandidateEditFragment()
    }

    private lateinit var viewModel: CandidateEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_candidate_edit, container, false)
    }


}