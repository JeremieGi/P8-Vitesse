package com.openclassrooms.p8vitesse.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.openclassrooms.p8vitesse.ui.list.CandidateListFragment


class PageAdapter(

    mainFragment: MainFragment// Fragment principal qui contient le PageAdapter

) : FragmentStateAdapter(mainFragment) {

    // Sert à passer en paramètre au fragment le filtre éventuellement saisi par l'utilisateur
    // à la création du fragment
    private var sValSearch : String = ""


    /**
     * Create an instance of fragment for each tab position
     * @param position : position on the tab (0 => first => all candidate, 1 => second => favorite candidate)
     */
    override fun createFragment(position: Int): Fragment {

        if (position==1){
            // Display favorite candidates
            return CandidateListFragment.newInstance(bOnlyFavorite = true,sValSearch)
        }
        // 1er tab = position 0
        // Display all candidates
        return CandidateListFragment.newInstance(bOnlyFavorite = false,sValSearch)

    }

    /**
     * @return : Number of tab
     */
    override fun getItemCount(): Int {
        return 2 // 2 tabs here : ALL / FAVORITE
    }

    /**
     * Set the research value
     */
    fun setValSearch(sValSearch: String) {
        this.sValSearch = sValSearch
    }

}
