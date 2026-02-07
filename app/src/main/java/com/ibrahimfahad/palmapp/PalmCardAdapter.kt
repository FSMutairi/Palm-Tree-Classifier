package com.ibrahimfahad.palmapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class PalmCard(val title: String, val description: String)

class PalmCardAdapter(private val cards: List<PalmCard>) :
    RecyclerView.Adapter<PalmCardAdapter.PalmViewHolder>() {

    class PalmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvCardTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvCardDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PalmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_palm_card, parent, false)
        return PalmViewHolder(view)
    }

    override fun onBindViewHolder(holder: PalmViewHolder, position: Int) {
        val card = cards[position]
        holder.tvTitle.text = card.title
        holder.tvDesc.text = card.description
    }

    override fun getItemCount(): Int = cards.size
}
