package com.neandril.realestatemanager.views.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup

import com.neandril.realestatemanager.R

/**
 * A simple [Fragment] subclass.
 */
class FilterFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        var dialog = dialog

        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            //setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
            dialog.setCancelable(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootview = inflater.inflate(R.layout.fragment_filter, container, false)
        // val button1 = rootview.findViewById<Button>(R.id.btnOK)


        val chipGroupTypes = rootview.findViewById<ChipGroup>(R.id.chipGroup_type)
        val chipGroupPois = rootview.findViewById<ChipGroup>(R.id.chipGroup_pois)
        val typeList = resources.getStringArray(R.array.estate_type)
        val poisList = resources.getStringArray(R.array.point_of_interest)

        for (type in typeList) {
            val chip = Chip(rootview.context)
            val chipDrawable = ChipDrawable.createFromAttributes(rootview.context, null, 0, R.style.Widget_MaterialComponents_Chip_Choice)
            chip.setChipDrawable(chipDrawable)
            chip.text = type
            chipGroupTypes.addView(chip)
        }

        for (poi in poisList) {
            val chip = Chip(rootview.context)
            val chipDrawable = ChipDrawable.createFromAttributes(rootview.context, null, 0, R.style.Widget_MaterialComponents_Chip_Filter)
            chip.setChipDrawable(chipDrawable)
            chip.text = poi
            chipGroupPois.addView(chip)
        }

        // button1.setOnClickListener { dismiss() }

        return rootview


    }


}
