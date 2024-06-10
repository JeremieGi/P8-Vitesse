package com.openclassrooms.p8vitesse.ui

import android.content.Context
import android.os.Bundle
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
import com.openclassrooms.p8vitesse.R
import com.openclassrooms.p8vitesse.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    // Binding
   private lateinit var binding: FragmentMainBinding

    // View Model
    private val viewModel: MainViewModel  by viewModels()

    // View Model partagé
    private val sharedViewModel: SharedViewModel by activityViewModels()

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

        binding.edtResearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {

                // Code à exécuter lorsque l'utilisateur appuie sur "Done"

                // On envoie la valeur dans un view model partagé (écouté par CnadidateListFragment)
                val inputText = binding.edtResearch.text.toString()
                sharedViewModel.setSearchQuery(inputText)

                // Fermer le clavier
                // TODO : Pourquoi ca ne se fait pas tout seul ?
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.edtResearch.windowToken, 0)

                true // Retourne true pour indiquer que l'action a été gérée
            } else {
                false
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




}