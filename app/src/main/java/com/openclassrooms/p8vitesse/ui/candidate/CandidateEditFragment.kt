package com.openclassrooms.p8vitesse.ui.candidate

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.p8vitesse.MainApplication
import com.openclassrooms.p8vitesse.R
import com.openclassrooms.p8vitesse.databinding.FragmentCandidateEditBinding
import com.openclassrooms.p8vitesse.domain.model.Candidate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


private const val ARG_CANDIDATE_ID = "paramID"

@AndroidEntryPoint
class CandidateEditFragment : Fragment() {

    companion object {
        fun newInstance() = CandidateEditFragment()

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    // Binding
    private lateinit var binding: FragmentCandidateEditBinding

    private val viewModel: CandidateEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCandidateEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // Récupération de l'ID du candidat
        var sIDCandidate = ""
        arguments?.let {

            sIDCandidate = it.getString(ARG_CANDIDATE_ID).toString()
            Log.d(MainApplication.TAG_DEBUG,"editFragment - loadCandidate ID : $sIDCandidate")
            viewModel.loadCandidate(sIDCandidate)

        }

        if (sIDCandidate.isEmpty()) {

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

        // T013 - Implement the top app bar title
        setupActionBar()

        // Save button
        binding.btnSave.setOnClickListener{

            try {

                val candidateNewData = candidateFromInputs()

                // Add Mode
                if (viewModel.bModeAdd()){

                    viewModel.add(candidateNewData)

                    // Close the fragment
                    requireActivity().onBackPressedDispatcher.onBackPressed()

                }
                else{

                }

            }
            catch (e: Exception) {

                displayError(e.message)

            }





        }


        // Date picker
        binding.btnPickDate.setOnClickListener{

            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)

            // date picker dialog
            val picker = DatePickerDialog(
                requireContext(),
                // Listener du date picker
                {
                    view, selectedYear, selectedMonth, selectedDay ->

                        // Date sélectionnée
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                        // DAte d'aujourd'hui
                        val todayCalendar = Calendar.getInstance()

                        // TODO : Voir avec Denis selectedMonth renvoie un mois comme si janvier = 0
                        // + dans le debugger ici selectedCalendar est toujours égale à la date du jour...

                        // Si date saisie dans le futur
                        if (selectedCalendar.after(todayCalendar)) {
                            Toast.makeText(requireContext(), "Vous ne pouvez pas sélectionner une date future", Toast.LENGTH_SHORT).show()
                        } else {
                            val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCalendar.time)
                            binding.edtDateOfBirth.setText(selectedDate)
                        }


                },
                // Init parameters
                year,
                month,
                day
            )
            picker.show()

        }


    }

    private fun displayError(sErrorMessage : String?) {

        sErrorMessage.let {

            // Afficher le Snackbar
            Snackbar.make(requireView(), "Erreur inattendue \n$it", Snackbar.LENGTH_LONG)
                .show()

        }

    }

    /**
     * Create a candidate instance with the input data
     */
    private fun candidateFromInputs(): Candidate {


        val dateOfBirth = dateFormatter.parse(binding.edtDateOfBirth.text.toString())

        return Candidate(
            id = viewModel.lIDCandidate,
            lastName = binding.edtLastName.text.toString(),
            firstName = binding.edtFirstName.text.toString(),
            phone = binding.edtPhone.text.toString(),
            email = binding.edtEmail.text.toString(),
            about = "",
            dateOfBirth = dateOfBirth,
            salaryExpectation = binding.edtExpectedSalary.text.toString().toInt(),
            note = binding.edtNote.text.toString(),
            topFavorite = false
        )

    }

    private fun bind(candidate: Candidate) {

        binding.edtLastName.setText(candidate.lastName)
        // TODO : Denis : je ne peux pas utiliser une syntaxe de ce type : binding.edtLastName.text = candidate.lastName

        binding.edtFirstName.setText(candidate.firstName)
        binding.edtPhone.setText(candidate.phone)
        binding.edtEmail.setText(candidate.email)
        binding.edtDateOfBirth.setText(dateFormatter.format(candidate.dateOfBirth))
        binding.edtExpectedSalary.setText(candidate.salaryExpectation)
        binding.edtNote.setText(candidate.note)

    }

    private fun setupActionBar() {

        // Set the toolbar as the support action bar
  //      (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

//        // Enable the Up button
//        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
//        actionBar?.setDisplayHomeAsUpEnabled(true)

        if (viewModel.bModeAdd()){
            // T013 - Implement the top app bar title
            binding.toolbar.title = getString(R.string.add_a_candidate)
        }
        else{
            //T038 - Implement the top app bar title
            binding.toolbar.title = getString(R.string.edit_a_candidate)
        }

        // T014 - Implement the top app bar navigation icon
        // Set up the click listener for the Up button
        binding.toolbar.setNavigationOnClickListener {
            // Handle the click event here
            //requireActivity().onBackPressed()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }


}