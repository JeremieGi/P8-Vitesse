package com.openclassrooms.p8vitesse.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.openclassrooms.p8vitesse.ui.list.CandidateListFragment


class PageAdapter(

    mainFragment: MainFragment // Fragment principal qui contient le PageAdapter

) : FragmentStateAdapter(mainFragment) {


    /**
     * Create an instance of fragment for each tab position
     * @param position : position on the tab (0 => first => all candidate, 1 => second => favorite candidate)
     */
    override fun createFragment(position: Int): Fragment {

        if (position==1){
            // Display favorite candidates
            return CandidateListFragment.newInstance(bOnlyFavorite = true)
        }
        // 1er tab = position 0
        // Display all candidates
        return CandidateListFragment.newInstance(bOnlyFavorite = false)

    }

    /**
     * @return : Number of tab
     */
    override fun getItemCount(): Int {
        return 2 // 2 tabs here : ALL / FAVORITE
    }

}
