package com.example.a5eddspellbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class Home : Fragment() {
    private lateinit var FavouriteSpellsAdapter: FavouriteAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val settingsPrefs = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        val backgroundColor = settingsPrefs.getInt("background_color", ContextCompat.getColor(requireContext(), R.color.BackgroundColor))
        val borderColor = settingsPrefs.getInt("border_color", ContextCompat.getColor(requireContext(), R.color.BorderColor))
        val textColor = settingsPrefs.getInt("text_color", ContextCompat.getColor(requireContext(), R.color.TextColor))


        view.findViewById<TextView>(R.id.HomeTitle)?.setTextColor(textColor)
        view.setBackgroundColor(backgroundColor)


        val favouriteSpellsList = view.findViewById<RecyclerView>(R.id.FavoriteSpells)

        FavouriteSpellsAdapter = FavouriteAdapter(emptyList() , textColor, borderColor)
        favouriteSpellsList.adapter = FavouriteSpellsAdapter
        favouriteSpellsList.layoutManager = LinearLayoutManager(context)

        val sharedPrefs = requireActivity().getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        val favoriteUrls: List<String> =
            sharedPrefs.getStringSet("favorite_spells", emptySet())
                ?.toList()
                ?: emptyList()
        val spells = mutableListOf<Spell>()

        lifecycleScope.launch {
            for (url in favoriteUrls) {
                try {
                    val json = withContext(Dispatchers.IO) {
                        URL("https://www.dnd5eapi.co$url").readText()
                    }
                    val spellObject = JSONObject(json)
                    val spellName = spellObject.getString("name")
                    val spellLevel = spellObject.getInt("level")
                    val spellUrl = spellObject.getString("url")
                    spells.add(
                        Spell(spellName, spellLevel, spellUrl)
                    )
                } catch (e: Exception) {
                    FavouriteSpellsAdapter.updateSpells(spells)
                }
            }
            FavouriteSpellsAdapter.updateSpells(spells)
        }
    }
}
