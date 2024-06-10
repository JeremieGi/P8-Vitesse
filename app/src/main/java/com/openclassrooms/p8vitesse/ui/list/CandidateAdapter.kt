package com.openclassrooms.p8vitesse.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.p8vitesse.R
import com.openclassrooms.p8vitesse.databinding.ItemCandidateBinding
import com.openclassrooms.p8vitesse.domain.model.Candidate
import java.time.format.DateTimeFormatter

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


    inner class CandidateViewHolder(
        private val binding: ItemCandidateBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(candidate: Candidate) {
            binding.itemCandidatePhoto.setImageResource(0)
            binding.itemCandidateNames.text = candidate.firstName+" "+candidate.lastName
            binding.itemCandidateNotes.text = candidate.note // TODO : Confirmer que c'est bien les notes à afficher
        }
    }


}