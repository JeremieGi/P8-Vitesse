package com.openclassrooms.p8vitesse.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.p8vitesse.databinding.ItemCandidateBinding
import com.openclassrooms.p8vitesse.domain.model.Candidate
import com.openclassrooms.p8vitesse.loadImageWithGlide

/**
 * Adapter of the Recycler View
 */
class CandidateAdapter(

    private var listCandidates: List<Candidate>,
    private val onItemClickListener: IOnItemClickListener

) :  RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder>(){


    /**
     * Inflate te layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCandidateBinding.inflate(layoutInflater, parent, false)
        return CandidateViewHolder(binding)
    }

    /**
     * Display an item values
     */
    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {

        val candidate = listCandidates[position]
        holder.bind(candidate)

    }

    /**
     * Number of candidate
     */
    override fun getItemCount(): Int {
        return listCandidates.size
    }

    /**
     * Refresh the recycler View
     */
    fun updateData(listCandidates: List<Candidate>) {
        this.listCandidates = listCandidates
        notifyDataSetChanged()
    }


    inner class CandidateViewHolder(
        private val binding: ItemCandidateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init{
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position)
                }
            }
        }


        /**
         * Display an item values
         */
        fun bind(candidate: Candidate) {

            //T007 - Display all candidates


            val sNames = "${candidate.firstName} ${candidate.lastName}"

            binding.itemCandidateNames.text = sNames
            binding.itemCandidateNotes.text = candidate.note


            // Charger l'image avec Glide
            if (candidate.photoFilePath.isNotEmpty()){
                // J'utilise le contexte de l'ImageView
                loadImageWithGlide(binding.itemCandidatePhoto.context, candidate.photoFilePath, binding.itemCandidatePhoto)
            }
        }
    }


}