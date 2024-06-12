package com.openclassrooms.p8vitesse.ui.candidate

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.openclassrooms.p8vitesse.MainApplication.Companion.TAG_DEBUG
import com.openclassrooms.p8vitesse.R
import com.openclassrooms.p8vitesse.databinding.FragmentCandidateDisplayBinding
import com.openclassrooms.p8vitesse.domain.model.Candidate
import com.openclassrooms.p8vitesse.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val ARG_CANDIDATE_ID = "paramID"

@AndroidEntryPoint
class CandidateDisplayFragment : Fragment() {

    // Binding
    private lateinit var binding: FragmentCandidateDisplayBinding

    // View Model
    private val viewModel: CandidateDisplayViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG_DEBUG,"displayFragment : onCreateView")
        binding = FragmentCandidateDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.d(TAG_DEBUG,"displayFragment : onViewCreated")

        super.onViewCreated(view, savedInstanceState)

        // Récupération de l'ID du candidat
        var sIDCandidate = ""
        arguments?.let {

            sIDCandidate = it.getString(ARG_CANDIDATE_ID).toString()
            viewModel.loadCandidate(sIDCandidate)

        }

        if (sIDCandidate.isEmpty()) {
            Toast.makeText(requireContext(), "No parameter", Toast.LENGTH_SHORT).show()
        }
        else{

            // Dans une coroutine, collecte du StateFlow
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.candidateFlow.collect { resultCandidate ->

                    resultCandidate?.let {
                        resultCandidate.onSuccess {
                            bind(it)
                        }.onFailure {
                            Toast.makeText(requireContext(), "Error login \n ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }

        }

    }

    private fun bind(candidate: Candidate) {

        Log.d(TAG_DEBUG,"Candidate ID = ${candidate.id} name = ${candidate.lastName}")

    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param lID : ID of the candidate
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(lID : Long) =
            CandidateDisplayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CANDIDATE_ID, lID.toString())
                }
            }
    }
}