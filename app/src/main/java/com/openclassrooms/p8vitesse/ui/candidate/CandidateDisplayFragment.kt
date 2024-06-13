package com.openclassrooms.p8vitesse.ui.candidate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.p8vitesse.MainApplication.Companion.TAG_DEBUG
import com.openclassrooms.p8vitesse.R
import com.openclassrooms.p8vitesse.databinding.FragmentCandidateDisplayBinding
import com.openclassrooms.p8vitesse.domain.model.Candidate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val ARG_CANDIDATE_ID = "paramID"

// Regarder çà pour les options de menu qui s'affichent pas : https://www.youtube.com/watch?v=xPzI0EPP07g


@AndroidEntryPoint
class CandidateDisplayFragment : Fragment() {

    // Binding
    private lateinit var binding: FragmentCandidateDisplayBinding

    // View Model
    private val viewModel: CandidateDisplayViewModel by viewModels()

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param lID : ID of the candidate
         */
        @JvmStatic
        fun newInstance(lID : Long) =
            CandidateDisplayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CANDIDATE_ID, lID.toString())
                }
            }
    }

/*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Indique que ce fragment a un menu d'options
    }
*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState);
        binding = FragmentCandidateDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)




        // Récupération de l'ID du candidat
        var sIDCandidate = ""
        arguments?.let {

            sIDCandidate = it.getString(ARG_CANDIDATE_ID).toString()
            Log.d(TAG_DEBUG,"displayFragment - loadCandidate ID : $sIDCandidate")
            viewModel.loadCandidate(sIDCandidate)

        }

        if (sIDCandidate.isEmpty()) {
            displayError("No parameter")
            onBack()
        }
        else{

            // Dans une coroutine, collecte du StateFlow
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.candidateFlow.collect { resultCandidate ->

                    resultCandidate?.let {
                        resultCandidate.onSuccess {

                            bind(it)
                            updateActionBarTitle(it)

                        }.onFailure {
                            Toast.makeText(requireContext(), "Error login \n ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }

        }

        setupActionBar()



        // Menus

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_candidate_display, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.itemFavorite -> {
                        // Gérer l'action de recherche
                        true
                    }
                    R.id.itemEdit -> {
                        // Gérer l'action des paramètres
                        true
                    }
                    R.id.itemDelete -> {
                        // Gérer l'action des paramètres
                        true
                    }
                    else -> false
                }
            }
        })

    }


    private fun updateActionBarTitle(candidate: Candidate) {

        //T024 - Implement the top app bar title
        requireActivity().title = "${candidate.firstName.capitalized()} ${candidate.lastName.uppercase()}"

    }

    private fun onBack() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun setupActionBar() {

  /*
        // T025 - Implement the top app bar navigation icon
        // Set up the click listener for the Up button
        requireActivity().actionBar.setNavigationOnClickListener {
            onBack()
        }
*/
    }

    /**
     * Display the candidate object in the fragment
     */
    private fun bind(candidate: Candidate) {

        Log.d(TAG_DEBUG,"Candidate ID = ${candidate.id} name = ${candidate.lastName}")

    }





    private fun displayError(sErrorMessage : String?) {

        sErrorMessage.let {

            // Afficher le Snackbar
            Snackbar.make(requireView(), "Erreur inattendue \n$it", Snackbar.LENGTH_LONG)
                .show()

        }

    }

    // Met la première lettre en majuscule
    // TODO : Pas de fonction existante dans Kotlin ? + la mettre à disposition de toutes les classes
    fun String.capitalized(): String {
        return this.replaceFirstChar {
            if (it.isLowerCase())
                it.uppercase()
            else it.toString()
        }
    }


}


