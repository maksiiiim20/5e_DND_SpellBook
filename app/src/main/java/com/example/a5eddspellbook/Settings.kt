package com.example.a5eddspellbook

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import androidx.core.content.edit

class Settings : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickBackgroundColorButton = view.findViewById<View>(R.id.PickBackgroundColorButton)
        val pickBorderColorButton = view.findViewById<View>(R.id.PickBorderColorButton)
        val pickTextColorButton = view.findViewById<View>(R.id.PickTextColorButton)

        val sharedPrefs = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        val bgColor = sharedPrefs.getInt("background_color", ContextCompat.getColor(requireContext(), R.color.BackgroundColor))
        val textColor = sharedPrefs.getInt("text_color", ContextCompat.getColor(requireContext(), R.color.TextColor))
        val borderColor = sharedPrefs.getInt("border_color", ContextCompat.getColor(requireContext(), R.color.BorderColor))

        applyBackgroundColor(view, bgColor)
        applyTextColor(view, textColor)
        applyBorderColor(view, borderColor)

        pickBackgroundColorButton.setOnClickListener {
            showColorPickerDialog("Background Color", "background_color") { color ->
                applyBackgroundColor(view, color)
            }
        }

        pickBorderColorButton.setOnClickListener {
            showColorPickerDialog("Border Color", "border_color") { color ->
                applyBorderColor(view, color)
            }
        }

        pickTextColorButton.setOnClickListener {
            showColorPickerDialog("Text Color", "text_color") { color ->
                applyTextColor(view, color)
            }
        }
    }

    private fun applyBackgroundColor(root: View, color: Int) {
        root.setBackgroundColor(color)
        updatePreviewButton(root.findViewById(R.id.PickBackgroundColorButton), color, false)
    }

    private fun applyTextColor(root: View, color: Int) {
        val textViews = listOf<TextView>(
            root.findViewById(R.id.SettingsTitle),
            root.findViewById(R.id.BackgroundColourLabel),
            root.findViewById(R.id.BorderColourLabel),
            root.findViewById(R.id.TextColourLabel)
        )
        textViews.forEach { it.setTextColor(color) }
        updatePreviewButton(root.findViewById(R.id.PickTextColorButton), color, true)
    }

    private fun applyBorderColor(root: View, color: Int) {
        val buttons = listOf<View>(
            root.findViewById(R.id.PickBackgroundColorButton),
            root.findViewById(R.id.PickBorderColorButton),
            root.findViewById(R.id.PickTextColorButton)
        )

        buttons.forEach { view ->
            val background = view.background as? GradientDrawable
            background?.setStroke(4, color)
        }
        
        updatePreviewButton(root.findViewById(R.id.PickBorderColorButton), color, true)
    }

    private fun updatePreviewButton(view: View?, color: Int, fillSolid: Boolean) {
        val drawable = view?.background as? GradientDrawable
        if (fillSolid) {
            drawable?.setColor(color)
        }
    }

    private fun showColorPickerDialog(title: String, prefKey: String, onColorSelected: (Int) -> Unit) {
        val sharedPrefs = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        ColorPickerDialog.Builder(requireContext())
            .setTitle(title)
            .setPreferenceName("${prefKey}Dialog")
            .setPositiveButton(getString(R.string.confirm), object : ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope, fromUser: Boolean) {
                    val selectedColor = envelope.color
                    onColorSelected(selectedColor)
                    sharedPrefs.edit { putInt(prefKey, selectedColor) }
                }
            })
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .show()
    }
}