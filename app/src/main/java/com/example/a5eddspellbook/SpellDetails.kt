package com.example.a5eddspellbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import kotlin.collections.remove
import kotlin.collections.toMutableSet

class SpellDetails : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.spell_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spellUrl = arguments?.getString("spellUrl")
        val favoriteBtn = view.findViewById<ImageButton>(R.id.favourite)
        val sharedPrefs = requireActivity().getSharedPreferences("Favorites", Context.MODE_PRIVATE)

        var isFavorite = sharedPrefs.getStringSet("favorite_spells", emptySet())?.contains(spellUrl) == true
        updateFavoriteIcon(favoriteBtn, isFavorite)
        favoriteBtn.setOnClickListener {
            val currentFavorites = sharedPrefs.getStringSet("favorite_spells", emptySet())?.toMutableSet() ?: mutableSetOf()

            if (isFavorite) {
                currentFavorites.remove(spellUrl)
            } else {
                currentFavorites.add(spellUrl)
            }


            sharedPrefs.edit().putStringSet("favorite_spells", currentFavorites).apply()

            isFavorite = !isFavorite
            updateFavoriteIcon(favoriteBtn, isFavorite)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val json = URL("https://www.dnd5eapi.co$spellUrl").readText()
            val spell = JSONObject(json)

            withContext(Dispatchers.Main) {
                val ritualView = view.findViewById<TextView>(R.id.Ritual)
                val materialView = view.findViewById<TextView>(R.id.Material)
                val concentrationView = view.findViewById<TextView>(R.id.Concentration)
                val upCastView = view.findViewById<TextView>(R.id.UpCast)

                view.findViewById<TextView>(R.id.Name).text = spell.getString("name")
                view.findViewById<TextView>(R.id.Description).text = joinJsonArray(spell.getJSONArray("desc"), "\n")

                val upCastArray = spell.optJSONArray("higher_level")

                if (upCastArray != null && upCastArray.length() > 0) {
                    upCastView.text = joinJsonArray(upCastArray, "\n")
                    upCastView.visibility = View.VISIBLE
                } else {
                    upCastView.visibility = View.GONE
                }

                view.findViewById<TextView>(R.id.Range).text = "Range: ${spell.getString("range")}"
                view.findViewById<TextView>(R.id.Components).text = "Components: ${joinJsonArray(spell.getJSONArray("components"), ", ")}"

                val materialText = spell.optString("material")
                if (materialText.isNotBlank()) {
                    materialView.text = "Materials: $materialText"
                    materialView.visibility = View.VISIBLE
                } else {
                    materialView.visibility = View.GONE
                }

                if (spell.getBoolean("ritual")) {
                    ritualView.text = "Ritual"
                    ritualView.visibility = View.VISIBLE
                } else {
                    ritualView.visibility = View.GONE
                }
                view.findViewById<TextView>(R.id.Duration).text = "Duration: ${spell.getString("duration")}"
                if (spell.getBoolean("concentration")) {
                    concentrationView.text = "Concentration"
                    concentrationView.visibility = View.VISIBLE
                } else {
                    concentrationView.visibility = View.GONE
                }
                view.findViewById<TextView>(R.id.CastingTime).text = "Casting Time: ${spell.getString("casting_time")}"
                view.findViewById<TextView>(R.id.Level).text = "Level: ${spell.getInt("level")}"
                view.findViewById<TextView>(R.id.SchoolOfMagic).text = "School of Magic: ${spell.getJSONObject("school").getString("name")}"
            }
        }
    }


}

private fun updateFavoriteIcon(btn: ImageButton, isFavorite: Boolean) {
    val icon = if (isFavorite) R.drawable.star_on else R.drawable.star_off
    btn.setImageResource(icon)
}

private fun joinJsonArray(jsonArray: JSONArray, separator: String): String {
    val sb = StringBuilder()
    for (i in 0 until jsonArray.length()) {
        if (i > 0) sb.append(separator)
        sb.append(jsonArray.getString(i))
    }
    return sb.toString()
}
