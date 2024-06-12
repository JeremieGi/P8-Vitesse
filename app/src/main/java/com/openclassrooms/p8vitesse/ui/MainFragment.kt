package com.openclassrooms.p8vitesse.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.openclassrooms.p8vitesse.MainApplication.Companion.TAG_DEBUG
import com.openclassrooms.p8vitesse.R
import com.openclassrooms.p8vitesse.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    // Binding
   private lateinit var binding: FragmentMainBinding

    // View Model
    private val viewModel: MainViewModel  by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        configureViewPagerAndTabs()

        // TODO : A la rotation de l'écran : Voir réaffecter la valeur recherchée du ViewModel vers le fragment
//        if (viewModel.searchValue.value != null && binding.edtResearch.text.toString().isEmpty()){
//            //binding.edtResearch.setText(viewModel.searchValue.value.toString())
//            // Appel manuel de l'observateur pour lancer initialement
//            // Marche pas car la valeur ne change pas donc l'observer n'est pas appelé
//            viewModel.setSearchValue(viewModel.searchValue.value.toString())
//
//        }

        binding.edtResearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {

                // Code à exécuter lorsque l'utilisateur appuie sur "Done"

                // On envoie la valeur dans un view model partagé (écouté par CnadidateListFragment)

//                val inputText = binding.edtResearch.text.toString()
//                try{
//                    viewModel.loadCandidates(inputText,binding.candidatelistViewpager.currentItem)
//                }
//                catch (e  : Exception){
//                    Log.d(TAG_DEBUG,"Exception : ${e.message}")
//                }

                // Fermer le clavier
                // TODO : Pourquoi ca ne se fait pas tout seul ? => Voir dans les configs de l'edit Text
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.edtResearch.windowToken, 0)

                true // Retourne true pour indiquer que l'action a été gérée
            } else {
                false
            }
       })


        binding.edtResearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                try{
                    //viewModel.requestCandidates(s.toString(),binding.candidatelistViewpager.currentItem)
                    viewModel.requestCandidates(s.toString())
                }
                catch (e  : Exception){
                    Log.d(TAG_DEBUG,"Exception : ${e.message}")
                }

            }
        })

        // Add a listener to handle tab selection events
        binding.candidatelistViewpagerTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                // Handle tab selected
                val position = tab.position
                // Do something when tab is selected
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Handle tab unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Handle tab reselected
                val position = tab.position
                // Do something when tab is reselected
                //viewModel.loadCandidates(binding.edtResearch.text.toString(),position)
            }
        })

    }

    private fun configureViewPagerAndTabs() {

        val viewPager2: ViewPager2 = binding.candidatelistViewpager
        val tabLayout: TabLayout = binding.candidatelistViewpagerTabs

        viewPager2.adapter = PageAdapter(this)


        // TabLayoutMediator
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            // Code lancé à l'init du fragment uniquement
            when (position) {
                0 -> tab.setText(getString(R.string.all))
                else -> tab.setText(getString(R.string.favorite))
            }
        }.attach()

    }


    // TODO : utiliser onSaveInstanceState(). https://developer.android.com/topic/libraries/architecture/saving-states?hl=fr


}