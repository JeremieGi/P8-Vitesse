package com.openclassrooms.p8vitesse.ui.candidate

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.p8vitesse.R
import com.openclassrooms.p8vitesse.TAG_DEBUG
import com.openclassrooms.p8vitesse.dStringToLocalDate
import com.openclassrooms.p8vitesse.databinding.FragmentCandidateEditBinding
import com.openclassrooms.p8vitesse.domain.model.Candidate
import com.openclassrooms.p8vitesse.sLocalDateToString
import com.openclassrooms.p8vitesse.saveImageToInternalStorage
import com.openclassrooms.p8vitesse.loadImageWithGlide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Calendar
import java.util.Date


private const val ARG_CANDIDATE_ID = "paramID"

@AndroidEntryPoint
class CandidateEditFragment : Fragment() {

    companion object {
        fun newInstance() = CandidateEditFragment()

    }

    // Binding
    private lateinit var binding: FragmentCandidateEditBinding

    private val viewModel: CandidateEditViewModel by viewModels()

    private var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>? = null

    // TODO : Je stocke l'URI ici (pas trop moyen de récupérer l'URI de puis l'ImageView apparement)
    private var sSelectedURI : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCandidateEditBinding.inflate(inflater, container, false)

        // Cette initialisation doit être faite avant l'initialisation graphique du fragment (onCreateView ou onAttach)
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {

                Log.d(TAG_DEBUG, "Selected photo : URI =  $uri")
                setImageWithURI(uri)

            } else {

                displayError(getString(R.string.no_media_selected))
            }
        }

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // Récupération de l'ID du candidat
        var sIDCandidate: String
        arguments?.let {

            sIDCandidate = it.getString(ARG_CANDIDATE_ID).toString()
            Log.d(TAG_DEBUG,"editFragment - loadCandidate ID : $sIDCandidate")
            viewModel.loadCandidate(sIDCandidate)

        }

        // Obverse the UI StateFow
        observeUIStateFlow()

        // T013 - Implement the top app bar title
        setupActionBar()

        // Save button
        binding.btnSave.setOnClickListener{
            saveCandidate()
        }

        setDatePickerListener()

        setImageListener()
    }

    private fun setImageListener() {

        // PhotoPicker
        binding.imgPhoto.setOnClickListener{

            val bPhotoPickerAvailable = isPhotoPickerAvailable(requireContext())
            if (bPhotoPickerAvailable){

                // Include only one of the following calls to launch(), depending on the types
                // of media that you want to let the user choose from.

                // Launch the photo picker and let the user choose only images.
                pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            }
            else{
                displayError(getString(R.string.no_photo_picker))
            }




        }

    }

    private fun setImageWithURI(uri: Uri) {

        sSelectedURI = uri.toString()

        // Charger l'image avec Glide
        Glide.with(this)
            .load(uri)
            .into(binding.imgPhoto)

    }


    private fun setDatePickerListener() {

        // Date picker
        binding.btnPickDate.setOnClickListener{

            val cldr: Calendar = Calendar.getInstance()

            var day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            var month: Int = cldr.get(Calendar.MONTH)
            var year: Int = cldr.get(Calendar.YEAR)

            // Si une date est saisie
            if (binding.edtDateOfBirth.text.toString().isNotEmpty()){
                // On se repositionne dessus
                val calendar = Calendar.getInstance()
                calendar.time = dStringToLocalDate(binding.edtDateOfBirth.text.toString())

                day = calendar.get(Calendar.DAY_OF_MONTH)
                month = calendar.get(Calendar.MONTH)
                year = calendar.get(Calendar.YEAR)
            }


            // date picker dialog
            val picker = DatePickerDialog(
                requireContext(),
                // Listener du date picker
                { view, selectedYear, selectedMonth, selectedDay ->

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
                        val sDate = sLocalDateToString(selectedCalendar.time)
                        binding.edtDateOfBirth.setText(sDate)
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

    private fun observeUIStateFlow() {
        // Dans une coroutine, collecte du StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.candidateStateFlow.collect { resultCandidateState ->

                when (resultCandidateState){

                    // Chargement du candidat
                    is CandidateUIState.Success -> {
                        val candidate = resultCandidateState.candidate
                        bind(candidate)
                        viewModel.currentCandidate = candidate
                    }

                    // Erreur lors du chargement du candidat
                    is CandidateUIState.Error -> {
                        displayError(resultCandidateState.exception.message)
                    }

                    // Opération d'ajout terminée avec succès
                    is CandidateUIState.OperationAddCompleted -> {
                        // Fermer le fragment
                        closeFragment()
                    }

                    // Opération de mise à jour terminée avec succès
                    is CandidateUIState.OperationUpdatedCompleted -> {
                        // Fermer le fragment
                        closeFragment()
                    }


                    else -> {
                        // NULL
                    }

                }

            }
        }
    }


    /**
     * Add or update a candidate
     */
    private fun saveCandidate() {

        // Les champs obligatoires sont bien remplis ?
        if (bCheckInputOK()){

            // Récupération d'un objet à partir de la saisie
            val candidateNewData = candidateFromInputs()

            // Add Mode
            if (viewModel.bModeAdd()){
                // Le résultat sera traité dans le StateFlow
                viewModel.add(candidateNewData)
            }
            else{
                viewModel.update(candidateNewData)
            }

        }
        // else : les champs apparaissent déjà en rouge


    }

    /**
     * Close this fragment
     */
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

    // Check the date
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

    // Check the email adress
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

    // Check the phone
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

    // Check the first name
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

    // Check the last name
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

    /**
     * Display error message in SnackBar
     */
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

        // Récupère la date au format Date
        var dateOfBirth = dStringToLocalDate(binding.edtDateOfBirth.text.toString())
        if (dateOfBirth==null){
            displayError("Impossible to convert : ${binding.edtDateOfBirth.text.toString()}")
            dateOfBirth = Date() // TODO : Je met la date du jour => A voir pour faire mieux
        }


        val expectedSalaryText = binding.edtExpectedSalary.text.toString()
        val nExpectedSalary = if (expectedSalaryText.isNotEmpty()) {
            expectedSalaryText.toInt()
        } else {
            0 // Gestion du salaire demandé non renseigné
        }


        // Une nouvelle image a été sélectionnée
        var sLocalPath : String
        if (sSelectedURI.isNotEmpty()){

            // TODO : Quelle couche gère les fichiers à supprimer par exemple

            // Crée un fichier local avec cette image
            sLocalPath = try{
                saveImageToInternalStorage(requireContext(), Uri.parse(sSelectedURI))
            } catch (e: IOException) {
                displayError(e.message)
                ""
            }

        }
        else{
            // Chemin du candidat OU vide si candidat null (Cas d'un ajout sans sélection d'image)
            sLocalPath = viewModel.currentCandidate?.photoFilePath ?: ""
        }


        return Candidate(
            id = viewModel.idCandidate,
            lastName = binding.edtLastName.text.toString(),
            firstName = binding.edtFirstName.text.toString(),
            phone = binding.edtPhone.text.toString(),
            email = binding.edtEmail.text.toString(),
            dateOfBirth = dateOfBirth,
            salaryExpectation = nExpectedSalary,
            note = binding.edtNote.text.toString(),
            topFavorite = false,
            photoFilePath = sLocalPath
        )

    }

    /**
     * Display the model data in the UI
     */
    private fun bind(candidate: Candidate) {

        binding.edtLastName.setText(candidate.lastName)
        binding.edtFirstName.setText(candidate.firstName)
        binding.edtPhone.setText(candidate.phone)
        binding.edtEmail.setText(candidate.email)
        binding.edtDateOfBirth.setText(sLocalDateToString(candidate.dateOfBirth))
        binding.edtExpectedSalary.setText(candidate.salaryExpectation.toString())
        binding.edtNote.setText(candidate.note)

        // Charger l'image avec Glide
        if (candidate.photoFilePath.isNotEmpty()){
            loadImageWithGlide(requireContext(), candidate.photoFilePath, binding.imgPhoto)
        }


    }

    /**
     * Paramètre l'Action Bar
     */
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