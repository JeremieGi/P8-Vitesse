package com.openclassrooms.p8vitesse.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.openclassrooms.p8vitesse.ui.CandidateListViewModel.Companion.ALL_CANDIDATE
import com.openclassrooms.p8vitesse.ui.CandidateListViewModel.Companion.FAVORITE_CANDIDATE


class PageAdapter(

    mainFragment: MainFragment

) : FragmentStateAdapter(mainFragment) {


    override fun createFragment(position: Int): Fragment {

        if (position==FAVORITE_CANDIDATE){
            return CandidateListFragment.newInstance(FAVORITE_CANDIDATE)
        }
        return CandidateListFragment.newInstance(ALL_CANDIDATE)

    }

    override fun getItemCount(): Int {
        return 2
    }

}
