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

        private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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


        // Dans une coroutine, collecte du StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.candidateStateFlow.collect { resultCandidateState ->

                when (resultCandidateState){

                    // Chargement du candidat
                    is CandidateState.Success -> {
                        val candidate = resultCandidateState.candidate
                        bind(candidate)
                    }

                    // Erreur lors du chargement du candidat
                    is CandidateState.Error -> {
                        displayError(resultCandidateState.exception.message)
                    }

                    // Opération de suppression terminée avec succès
                    is CandidateState.OperationAddCompleted -> {
                        // Fermer le fragment
                        closeFragment()
                    }

                    else -> {
                        // NULL
                    }

                }

            }
        }




        // T013 - Implement the top app bar title
        setupActionBar()

        // Save button
        binding.btnSave.setOnClickListener{
            addCandidate()
        }


        // Date picker
        binding.btnPickDate.setOnClickListener{

            val cldr: Calendar = Calendar.getInstance()

            // TODo : Si une date est saisie, il serait bien de repositionner le picker sur cette date
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
                            displayError(getString(R.string.impossible_to_selected_a_date_of_birth_in_the_future))
                        } else {
                            val selectedDate = dateFormatter.format(selectedCalendar.time)
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


    private fun addCandidate() {


        if (bCheckInputOK()){

            val candidateNewData = candidateFromInputs()

            // Add Mode
            if (viewModel.bModeAdd()){
                // Le résultat sera traité dans le StateFlow
                viewModel.add(candidateNewData)
            }
            else{

            }

        }
        // else : les champs apparaissent en rouge


    }

    private fun closeFragment() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    /**
     * Vérifie que les champs sont saisis
     */
    private fun bCheckInputOK(): Boolean {

        // T023 - Check the information

        var bImputsOK = true

        if (! inputLastNameOK()){
            bImputsOK = false
        }

        if (! inputFirstNameOK()){
            bImputsOK = false
        }

        if (! inputPhoneOK()){
            bImputsOK = false
        }

        if (! inputEmailOK()){
            bImputsOK = false
        }

        if (! inputDateOK()){
            bImputsOK = false
        }

        return bImputsOK

    }

    private fun inputDateOK(): Boolean {

        var bImputsOK = true

        val inputDate = binding.edtDateOfBirth.text.toString().trim()
        if (inputDate.isEmpty()) {
            binding.inputLayoutDateOfBirth.error = getString(R.string.mandatory_field)
            binding.inputLayoutDateOfBirth.isErrorEnabled = true
            bImputsOK = false
        } else {
            binding.inputLayoutDateOfBirth.error = null
            binding.inputLayoutDateOfBirth.isErrorEnabled = false
        }

        return bImputsOK
    }

    private fun inputEmailOK(): Boolean {

        var bImputsOK = true

        val inputEmail = binding.edtEmail.text.toString().trim()
        if (inputEmail.isEmpty()) {
            binding.inputLayoutEmail.error = getString(R.string.mandatory_field)
            binding.inputLayoutEmail.isErrorEnabled = true
            bImputsOK = false
        } else {

            // Mail correct
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()){

                binding.inputLayoutEmail.error = null
                binding.inputLayoutEmail.isErrorEnabled = false

            }
            else{

                binding.inputLayoutEmail.error = getString(R.string.invalid_format)
                binding.inputLayoutEmail.isErrorEnabled = true
                bImputsOK = false

            }
        }

        return bImputsOK

    }

    private fun inputPhoneOK(): Boolean {

        var bImputsOK = true

        val inputPhone = binding.edtPhone.text.toString().trim()
        if (inputPhone.isEmpty()) {
            binding.inputLayoutPhone.error = getString(R.string.mandatory_field)
            binding.inputLayoutPhone.isErrorEnabled = true
            bImputsOK = false
        } else {
            binding.inputLayoutPhone.error = null
            binding.inputLayoutPhone.isErrorEnabled = false
        }

        return bImputsOK

    }

    private fun inputFirstNameOK(): Boolean {

        var bImputsOK = true

        val inputFirstName = binding.edtFirstName.text.toString().trim()
        if (inputFirstName.isEmpty()) {
            binding.inputLayoutFirstName.error = getString(R.string.mandatory_field)
            binding.inputLayoutFirstName.isErrorEnabled = true
            bImputsOK = false
        } else {
            binding.inputLayoutFirstName.error = null
            binding.inputLayoutFirstName.isErrorEnabled = false
        }

        return bImputsOK
    }

    private fun inputLastNameOK(): Boolean {

        var bImputsOK = true

        val inputLastName = binding.edtLastName.text.toString().trim()
        if (inputLastName.isEmpty()) {
            binding.inputLayoutLastName.error = getString(R.string.mandatory_field)
            binding.inputLayoutLastName.isErrorEnabled = true
            bImputsOK = false

        } else {
            binding.inputLayoutLastName.error = null
            binding.inputLayoutLastName.isErrorEnabled = false
        }

        return bImputsOK

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

        // Gestion du salaire demandé non renseigné
        val expectedSalaryText = binding.edtExpectedSalary.text.toString()
        val nExpectedSalary = if (expectedSalaryText.isNotEmpty()) {
            expectedSalaryText.toInt()
        } else {
            0
        }

        return Candidate(
            id = viewModel.lIDCandidate,
            lastName = binding.edtLastName.text.toString(),
            firstName = binding.edtFirstName.text.toString(),
            phone = binding.edtPhone.text.toString(),
            email = binding.edtEmail.text.toString(),
            dateOfBirth = dateOfBirth,
            salaryExpectation = nExpectedSalary,
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


        if (viewModel.bModeAdd()){
            // T013 - Implement the top app bar title
            binding.toolbarEdit.title = getString(R.string.add_a_candidate)
        }
        else{
            //T038 - Implement the top app bar title
            binding.toolbarEdit.title = getString(R.string.edit_a_candidate)
        }

        // T014 - Implement the top app bar navigation icon
        // Set up the click listener for the Up button
        binding.toolbarEdit.setNavigationOnClickListener {
            // Handle the click event here
            //requireActivity().onBackPressed()
            closeFragment()
        }

    }


}