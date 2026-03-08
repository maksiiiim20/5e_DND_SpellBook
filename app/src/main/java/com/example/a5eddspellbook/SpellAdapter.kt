package com.example.a5eddspellbook

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView





class SpellAdapter(private var spells: List<Spell>, private var textColor: Int, private var borderColor: Int) : RecyclerView.Adapter<SpellAdapter.SpellViewHolder>() {

    class SpellViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val spellName: TextView = view.findViewById(R.id.spellName)
        val spellLevel: TextView = view.findViewById(R.id.spellLevel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpellViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.spell_list_item, parent, false)
        return SpellViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpellViewHolder, index: Int) {
        val spell = spells[index]
        holder.itemView.setOnClickListener {
            val activity = it.context as AppCompatActivity
            val fragment = SpellDetails().apply {
                arguments = Bundle().apply {
                    putString("spellUrl", spell.url)
                }
            }
            activity.supportFragmentManager.commit {
                replace(R.id.FragmentContainer, fragment)
            }
        }
        holder.spellName.text = spell.name
        holder.spellLevel.text = "Level: ${spell.level}"
        (holder.itemView.background as? GradientDrawable)?.apply {
            mutate()
            setStroke(4, borderColor)
        }
        holder.spellName.setTextColor(textColor)
        holder.spellLevel.setTextColor(textColor)
    }

    override fun getItemCount() = spells.size

    fun updateSpells(newSpells: List<Spell>) {
        spells = newSpells
        notifyDataSetChanged()
    }
}