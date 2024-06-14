package com.openclassrooms.p8vitesse.ui.candidate

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.p8vitesse.R
import com.openclassrooms.p8vitesse.TAG_DEBUG
import com.openclassrooms.p8vitesse.capitalized
import com.openclassrooms.p8vitesse.databinding.FragmentCandidateDisplayBinding
import com.openclassrooms.p8vitesse.domain.model.Candidate
import com.openclassrooms.p8vitesse.sLocalDateToString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
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
            displayError(getString(R.string.no_parameter))
            closeFragment()
        }
        else{

            observeStateFlow()

        }


        setupActionBarAndMenus()

        binding.btnCall.setOnClickListener{
            callCandidate()
        }

        binding.btnSMS.setOnClickListener{
            sendSMStoCandidate()
        }

        binding.btnSentEmail.setOnClickListener{
            sendEmailtoCandidate()
        }
    }

    // T033 - Send an email to the candidate
    private fun sendEmailtoCandidate() {

        // The email is a required field (never empty)
        val sEmail = viewModel.getCurrentCandidate().phone

        // Doc ici : https://developer.android.com/guide/components/intents-common?hl=fr
        val mIntent = Intent(Intent.ACTION_SEND) // ACTION_SENDTO => Ca me dit : pas d'application de messagerie..
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"

        // TODO : D'après la doc, çà devrait mettre la valeur dans le champ destinataire
        //  mais :
        //  dans mon appli Samsung => çà le met dans l'expéditeur
        //  dans l'appli Gmail çà met rien
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(sEmail))

        //put the Subject in the intent
 //       mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        //put the message in the intent
 //       mIntent.putExtra(Intent.EXTRA_TEXT, message)


        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, getString(R.string.select_email_application)))
        }
        catch (e: Exception){
            Toast.makeText(context, "${getString(R.string.no_application_of_email_found)}\n${e.message}", Toast.LENGTH_SHORT).show()
        }



    }

    // T032 - Send an SMS to the candidate
    private fun sendSMStoCandidate() {

        // The phone number is a required field (never empty)
        val sPhoneNumber = viewModel.getCurrentCandidate().phone

        val smsUri = Uri.parse("smsto:$sPhoneNumber")
        val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
       // smsIntent.putExtra("sms_body", "Votre message ici") // Corps du message facultatif

        // Vérifie si l'activité peut être gérée
        if (smsIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(smsIntent)
        } else {
            // Gérer le cas où aucune application de messagerie SMS n'est disponible
            Toast.makeText(context, getString(R.string.no_sms_application_found), Toast.LENGTH_SHORT).show()
        }
    }

    // T031 - Call the candidate
    private fun callCandidate() {

        // The phone number is a required field (never empty)
        val sPhoneNumber = viewModel.getCurrentCandidate().phone

        val intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse("tel:$sPhoneNumber"))
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireActivity(), R.string.phone_not_found, Toast.LENGTH_SHORT).show()
        }

    }

    private fun observeStateFlow() {
        // Dans une coroutine, collecte du StateFlow avec le candidat chargé
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.candidateStateFlow.collect { resultCandidateState ->

                when (resultCandidateState){

                    // Chargement du candidat
                    is CandidateUIState.Success -> {
                        val candidate = resultCandidateState.candidate
                        bind(candidate)
                        updateActionBarTitle(candidate)
                    }

                    // Erreur lors du chargement du candidat
                    is CandidateUIState.Error -> {
                        displayError(resultCandidateState.exception.message)
                    }

                    // Opération de suppression terminée avec succès
                    is CandidateUIState.OperationDeleteCompleted -> {
                        // Fermer le fragment
                        closeFragment()
                    }

                    is CandidateUIState.Conversion -> {
                        binding.tvSalaryConversion.text = resultCandidateState.resultConversion
                    }

                    else -> {
                        // NULL
                    }

                }


            }
        }
    }


    private fun deleteCandidateWithConfirmation() {


        val builder = AlertDialog.Builder(requireContext())
        //builder.setTitle("Confirmation de suppression")
        builder.setMessage(getString(R.string.delete_confirmation))

        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->

            viewModel.delete()

        }

        builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
            // Fermer la boîte de dialogue
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()

    }


    private fun updateActionBarTitle(candidate: Candidate) {

        //T024 - Implement the top app bar title
        binding.toolbarDisplay.title = "${candidate.firstName.capitalized()} ${candidate.lastName.uppercase()}"

    }

    private fun closeFragment() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun setupActionBarAndMenus() {

        // TODO : Pas une syntaxe plus simple ?
        // Trouver et configurer la Toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarDisplay)

        // Activer le bouton de retour
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // T025 - Implement the top app bar navigation icon
        // Set up the click listener for the Up button
        binding.toolbarDisplay.setNavigationOnClickListener {
            closeFragment()
        }


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
                        // Supprime le candidat
                        deleteCandidateWithConfirmation()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    /**
     * Display the candidate object in the fragment
     */
    private fun bind(candidate: Candidate) {

        Log.d(TAG_DEBUG,"Candidate ID = ${candidate.id} name = ${candidate.lastName}")

        val sDateOfBirth = sLocalDateToString(candidate.dateOfBirth)

        binding.tvBithdayAndAge.text = "${sDateOfBirth} (${candidate.nAge()} ${getString(R.string.year)})"

        // T035 - Display the expected salary of the candidate
        val sSalary = candidate.salaryExpectation.toString()

        val deviseFrom = Currency.getInstance(Locale.getDefault())
        binding.tvExpectedSalaryValue.text = "$sSalary ${deviseFrom.symbol}"

        viewModel.conversion(deviseFrom.currencyCode, candidate.salaryExpectation.toDouble())

//        when (devise.symbol) {
//            "EUR" -> {
//                viewModel.conversion(ICurrencyAPI.CURRENCY_CODE_EURO, candidate.salaryExpectation.toDouble())
//                // Résultat asynchrone affiché dans le traitement du UI State
//            }
//            "GBP" -> {
//                viewModel.conversion(ICurrencyAPI.CURRENCY_CODE_EURO, candidate.salaryExpectation.toDouble())
//            }
//            else -> {
//
//            }
//        }




        binding.tvNotesValues.text = candidate.note

    }





    private fun displayError(sErrorMessage : String?) {

        sErrorMessage.let {

            // Afficher le Snackbar
            Snackbar.make(requireView(),"${getString(R.string.error)}\n$it", Snackbar.LENGTH_LONG)
                .show()

        }

    }




}


