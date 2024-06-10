package com.openclassrooms.p8vitesse.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.p8vitesse.databinding.FragmentCandidateListBinding
import com.openclassrooms.p8vitesse.ui.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CandidateListFragment(

    private val nFavorite : Int = CandidateListViewModel.ALL_CANDIDATE

) : Fragment() {

    companion object {

        fun newInstance(favoriteCandidate: Int): Fragment {
            return CandidateListFragment(favoriteCandidate)
        }
    }

    // Binding
    private lateinit var binding: FragmentCandidateListBinding

    // View Model
    private val viewModel: CandidateListViewModel by viewModels()

    // Recycler View
    private val candidatesAdapter = CandidateAdapter(emptyList())

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCandidateListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        viewModel.setFavoriteMode(nFavorite)

        binding.recyclerViewCandidates.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCandidates.adapter = candidatesAdapter

        // Observe search query and update the list
        sharedViewModel.searchQuery.observe(viewLifecycleOwner, Observer { query ->
            candidatesAdapter.filter(query)
        })

        // Launch the UI States observer
        observeUiStates()

        // Load candidates (the observer will be notified)
        viewModel.loadAllCandidates()

    }

    /**
    * Launch the UI States observer
    */
    private fun observeUiStates() {

        // in the fragment view scope
        viewLifecycleOwner.lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Collect the uiState
                viewModel.uiState.collect {

                    // it est ici de type CandidateListUIStates

                    binding.progressbarLoading.isVisible = false
                    binding.tvEmptyList.isVisible = false
                    binding.recyclerViewCandidates.isVisible = true

                    // Premier appel avec objet vide
                    if (!it.isLoading && (it.errorMessage==null) && it.listCandidates.isEmpty()) {
                        // A cause de l'initialisation du CandidateListUIStates
                    }
                    else{

                        // reception of the database flow

                        // is Loading
                        if (it.isLoading){

                            // T003 - Loading state
                            binding.progressbarLoading.isVisible = true

                        }
                        else{

                            // Error
                            if (it.errorMessage?.isNotBlank() == true) {

                                Snackbar.make(binding.root, it.errorMessage, Snackbar.LENGTH_LONG)
                                    .show()

                            }
                            else{

                                // No candidate in the database
                                if (it.listCandidates.isEmpty()){

                                    //T004 - Empty state
                                    binding.tvEmptyList.isVisible = true
                                    binding.recyclerViewCandidates.isVisible = false

                                }

                                // Mise à jour du recycler view
                                candidatesAdapter.updateData(it.listCandidates)

                            }

                        }
                    }

                }

            }


        }
    }


}