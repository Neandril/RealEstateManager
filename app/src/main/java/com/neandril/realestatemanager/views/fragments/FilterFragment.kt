package com.neandril.realestatemanager.views.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.FilterModel
import com.neandril.realestatemanager.utils.toSquare
import com.neandril.realestatemanager.utils.toThousand
import com.neandril.realestatemanager.viewmodels.EstateViewModel
import com.neandril.realestatemanager.viewmodels.FilterInteractionViewModel
import com.neandril.realestatemanager.views.adapters.MainRecyclerViewAdapter

class FilterFragment : BottomSheetDialogFragment() {

    private lateinit var estateViewModel: EstateViewModel
    private val filterInteraction: FilterInteractionViewModel by activityViewModels()

    private lateinit var seekbarPrice: CrystalRangeSeekbar
    private lateinit var seekbarSurface: CrystalRangeSeekbar
    private lateinit var chipGroupTypes: ChipGroup
    private lateinit var chipGroupPois: ChipGroup

    private var filter: FilterModel? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_filter, container, false)

        root.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
/*            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED*/
            dialog.setCancelable(false)

            // Make the bottom sheet to fill the half of the screen (div by 2)
/*            val newHeight = activity?.window?.decorView?.measuredHeight
            val viewGroupLayoutParams = bottomSheet.layoutParams
            viewGroupLayoutParams.height = newHeight ?: 0
            bottomSheet.layoutParams = viewGroupLayoutParams*/
        }

        if (filter != null) {
            Log.d("Filter", "Filter: not null")
        } else {
            Log.d("Filter", "Filter: null")
        }


        configureSeekbars(root)
        configureChips(root)
        configureButtonsClicks(root)

        return root
    }

    private fun configureSeekbars(view: View) {
        seekbarPrice = view.findViewById(R.id.seekbar_price)
        seekbarSurface = view.findViewById(R.id.seekbar_surface)
        val tvMinPrice = view.findViewById<TextView>(R.id.textview_filter_min_price)
        val tvMaxPrice = view.findViewById<TextView>(R.id.textview_filter_max_price)
        val tvMinSurface = view.findViewById<TextView>(R.id.textview_filter_min_surface)
        val tvMaxSurface = view.findViewById<TextView>(R.id.textview_filter_max_surface)

        seekbarPrice.setMaxValue(140000000F)
        seekbarPrice.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            tvMinPrice.text = minValue.toString().toThousand()
            tvMaxPrice.text = maxValue.toString().toThousand()
        }

        seekbarSurface.setMaxValue(1000F)
        seekbarSurface.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            tvMinSurface.text = minValue.toString().toSquare()
            tvMaxSurface.text = maxValue.toString().toSquare()
        }
    }

    private fun configureButtonsClicks(view: View) {
        val btnOk = view.findViewById<TextView>(R.id.textView_btnOk)
        val btnCancel = view.findViewById<TextView>(R.id.textView_cancel)
        val btnResetAll = view.findViewById<TextView>(R.id.textView_restalAll)
        val editTextRooms = view.findViewById<EditText>(R.id.edittext_filter_rooms)
        val editTextLocality = view.findViewById<EditText>(R.id.edittext_filter_locality)
        val cbIsSold = view.findViewById<CheckBox>(R.id.checkbox_isSold)
        val cbPhotos = view.findViewById<CheckBox>(R.id.checkbox_photos)

        btnOk.setOnClickListener {
            val filterRooms = if (!editTextRooms.text.isNullOrEmpty()) editTextRooms.text.toString().toInt() else 0
            val filterLocality = if (!editTextLocality.text.isNullOrEmpty()) getString(R.string.filter_locality_format, editTextLocality.text.toString()) else getString(R.string.null_or_empty)
            val minPrice = seekbarPrice.selectedMinValue.toInt()
            val maxPrice = seekbarPrice.selectedMaxValue.toInt()
            val minSurface = seekbarSurface.selectedMinValue.toInt()
            val maxSurface = seekbarSurface.selectedMaxValue.toInt()

            val filter = FilterModel(minPrice, maxPrice, minSurface, maxSurface,
                filterRooms, getSelectedChips(chipGroupTypes), getSelectedChips(chipGroupPois),
                filterLocality, cbIsSold.isChecked, cbPhotos.isChecked)

            filterInteraction.setFilter(filter)

            dialog?.dismiss()
        }

        btnCancel.setOnClickListener {
            Log.d("FilterFragment", "Clicked on: Cancel")

            dialog?.dismiss()
        }

        btnResetAll.setOnClickListener {
            Log.d("FilterFragment", "Clicked on: Reset all")
        }
    }

    private fun configureChips(view: View) {
        chipGroupTypes = view.findViewById(R.id.chipGroup_type)
        chipGroupPois = view.findViewById(R.id.chipGroup_pois)
        val typeList = resources.getStringArray(R.array.estate_type)
        val poisList = resources.getStringArray(R.array.point_of_interest)

        // Chips for estate type are ChipChoice
        for (type in typeList) {
            val chip = Chip(context)
            val chipDrawable = ChipDrawable.createFromAttributes(context!!, null, 0, R.style.Widget_MaterialComponents_Chip_Choice)
            chip.setChipDrawable(chipDrawable)
            chip.text = type
            chipGroupTypes.addView(chip)
        }

        // Chips for points of interest are ChipFilter
        for (poi in poisList) {
            val chip = Chip(context)
            val chipDrawable = ChipDrawable.createFromAttributes(context!!, null, 0, R.style.Widget_MaterialComponents_Chip_Filter)
            chip.setChipDrawable(chipDrawable)
            chip.text = poi
            chipGroupPois.addView(chip)
        }
    }

    private fun getSelectedChips(chipGroup: ChipGroup) : MutableList<String> {
        val selectedChips = mutableListOf<String>()
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            if (chip.isChecked)
            {
                selectedChips.add(chip.text.toString())
            }
        }

        return selectedChips
    }

    private fun getMaxPrice() {
        estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
/*        estateViewModel.getMaxPrice().observe(this, Observer {
            maxValue = it.price.toFloat()
            Log.d("MaxPrice", "MaxPrice: " + it.price)
        })*/
    }
}
