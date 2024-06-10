package com.openclassrooms.p8vitesse.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.p8vitesse.databinding.ItemCandidateBinding
import com.openclassrooms.p8vitesse.domain.model.Candidate


class CandidateAdapter(

    private var listCandidates: List<Candidate>

) :  RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCandidateBinding.inflate(layoutInflater, parent, false)
        return CandidateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {

        val candidate = listCandidates[position]
        holder.bind(candidate)

    }

    override fun getItemCount(): Int {
        return listCandidates.size
    }

    fun updateData(listCandidates: List<Candidate>) {
        this.listCandidates = listCandidates
        notifyDataSetChanged()
    }

    /**
     * T010 - Filter candidates : TODO : discuter avec Denis car peut-être fait avec une requête aussi (il faudrait rappeler le Repository)
     */
    fun filter(sFilterName: String) {
        // Si pas de filtre
        listCandidates = if (sFilterName.isEmpty()) {
            // On prend la l
            listCandidates
        } else {
            listCandidates.filter {
                it.firstName.contains(sFilterName, ignoreCase = true) ||it.lastName.contains(sFilterName, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }


    inner class CandidateViewHolder(
        private val binding: ItemCandidateBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(candidate: Candidate) {

            //T007 - Display all candidates

            // TODO : Gérer l'image ici

            binding.itemCandidatePhoto.setImageResource(0)
            binding.itemCandidateNames.text = candidate.firstName+" "+candidate.lastName
            binding.itemCandidateNotes.text = candidate.note
        }
    }


}