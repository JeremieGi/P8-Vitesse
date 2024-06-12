package com.openclassrooms.p8vitesse.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.p8vitesse.MainApplication.Companion.TAG_DEBUG
import com.openclassrooms.p8vitesse.R
import com.openclassrooms.p8vitesse.databinding.FragmentCandidateListBinding
import com.openclassrooms.p8vitesse.ui.candidate.CandidateDisplayFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CandidateListFragment(

    private val bOnlyFavorite : Boolean = false

) : Fragment(), IOnItemClickListener {

    companion object {

        fun newInstance(bOnlyFavorite : Boolean): Fragment {
            return CandidateListFragment(bOnlyFavorite)
        }
    }

    // Binding
    private lateinit var binding: FragmentCandidateListBinding

    // View Model
    private val viewModel: CandidateListViewModel by viewModels()

    // Recycler View
    private val candidatesAdapter = CandidateAdapter(emptyList(),this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCandidateListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG_DEBUG,"  onViewCreated bOnlyFavorite = $bOnlyFavorite")

        viewModel.bFavoriteOnly = bOnlyFavorite

        binding.recyclerViewCandidates.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCandidates.adapter = candidatesAdapter


        // Launch the UI States observer
        observeUiStates()


        viewModel.observeCandidates()

        // Load candidates (the observer will be notified)
        viewModel.initCandidates()

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

    /**
     * Clic sur le recyclerView
     */
    override fun onItemClick(position: Int) {

        // Récupération du candidat
        val selectedCandidate = viewModel.uiState.value.listCandidates[position]
        // Récupération de son id
        val id = selectedCandidate.id?:0
        // Instanciation du fragment
        val fragmentFiche = CandidateDisplayFragment.newInstance(id)

        // parentFragment => pour remonter au MainFragment
        // parentFragmentManager => pour remonter au container de l'activity qui contient MainFragment
        parentFragment?.parentFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragmentFiche)
            ?.addToBackStack(null)
            ?.commit()

    }


}