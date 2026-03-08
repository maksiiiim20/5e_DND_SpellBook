package com.example.a5eddspellbook

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class Search : Fragment() {

    private lateinit var spellAdapter: SpellAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsPrefs = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val backgroundColor = settingsPrefs.getInt("background_color", ContextCompat.getColor(requireContext(), R.color.BackgroundColor))
        val borderColor = settingsPrefs.getInt("border_color", ContextCompat.getColor(requireContext(), R.color.BorderColor))
        val textColor = settingsPrefs.getInt("text_color", ContextCompat.getColor(requireContext(), R.color.TextColor))

        val title = view.findViewById<TextView>(R.id.SearchTitle)
        title?.setTextColor(textColor)

        val searchField = view.findViewById<EditText>(R.id.searchField)
        searchField?.setTextColor(textColor)
        searchField?.setHintTextColor(textColor)
        (searchField?.background as? GradientDrawable)?.apply {
            mutate()
            setStroke(4, borderColor)
        }
        view.setBackgroundColor(backgroundColor)



        val searchResultsList = view.findViewById<RecyclerView>(R.id.SearchResultsList)

        spellAdapter = SpellAdapter(emptyList() , textColor, borderColor)
        searchResultsList.adapter = spellAdapter
        searchResultsList.layoutManager = LinearLayoutManager(context)

        searchField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentText = s.toString()
                if (currentText.isNotBlank()) {
                    lifecycleScope.launch {
                        try {
                            val json = withContext(Dispatchers.IO) {
                                URL("https://www.dnd5eapi.co/api/spells/?name=$currentText").readText()
                            }
                            val resultsArray = JSONObject(json).getJSONArray("results")
                            val spells = (0 until resultsArray.length()).map { i ->
                                val spellObject = resultsArray.getJSONObject(i)
                                val spellName = spellObject.getString("name")
                                val spellLevel = spellObject.getInt("level")
                                val spellUrl = spellObject.getString("url")
                                Spell(spellName, spellLevel, spellUrl)
                            }
                            withContext(Dispatchers.Main) {
                                spellAdapter.updateSpells(spells)
                            }
                        } catch (e: Exception) {
                                spellAdapter.updateSpells(emptyList())
                        }
                    }
                } else {
                     lifecycleScope.launch(Dispatchers.Main){
                        spellAdapter.updateSpells(emptyList())
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }
            override fun afterTextChanged(s: Editable?) {
                // Not used
            }
        })
    }
}
