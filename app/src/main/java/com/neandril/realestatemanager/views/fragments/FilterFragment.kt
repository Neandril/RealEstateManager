package com.neandril.realestatemanager.views.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import kotlinx.android.synthetic.main.fragment_filter.*
import org.w3c.dom.Text

class FilterFragment : BottomSheetDialogFragment() {

    private lateinit var estateViewModel: EstateViewModel
    private val filterInteraction: FilterInteractionViewModel by activityViewModels()

    private lateinit var seekbarPrice: CrystalRangeSeekbar
    private lateinit var seekbarSurface: CrystalRangeSeekbar
    private lateinit var chipGroupTypes: ChipGroup
    private lateinit var chipGroupPois: ChipGroup
    private lateinit var tvMinPrice: TextView
    private lateinit var tvMaxPrice: TextView
    private lateinit var tvMinSurface: TextView
    private lateinit var tvMaxSurface: TextView
    private lateinit var btnOk: TextView
    private lateinit var btnCancel: TextView
    private lateinit var btnResetAll: TextView
    private lateinit var editTextRooms: EditText
    private lateinit var editTextLocality: EditText
    private lateinit var cbIsSold: CheckBox
    private lateinit var cbPhotos: CheckBox


    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_filter, container, false)

        root.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            dialog.setCancelable(false)

        }

        estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)

        configureSeekbars(root)
        configureChips(root)
        configureButtonsClicks(root)

        filterInteraction.getFilter().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                fillFragment(it)
            }
        })

        return root
    }

    private fun fillFragment(filterModel: FilterModel?) {
        if (filterModel != null) {
            Log.d("Filter", "minProce: " + filterModel.minPrice + ", maxPrice: " + filterModel.maxPrice)
            tvMinPrice.text = filterModel.minPrice.toString()
            tvMaxPrice.text = filterModel.maxPrice.toString()
            tvMinSurface.text = filterModel.minSurface.toString()
            tvMaxSurface.text = filterModel.maxSurface.toString()

            if (filterModel.nbRooms != 0) editTextRooms.setText(filterModel.nbRooms.toString()) else editTextRooms.setText("")
            if (filterModel.location != "") editTextLocality.setText(filterModel.location.toString()) else editTextLocality.setText("")

            cbIsSold.isChecked = filterModel.isSold
            cbPhotos.isChecked = filterModel.displayOnlyPhotos

            for (i in 0 until chipGroupTypes.childCount) {
                val chip = chipGroupTypes.getChildAt(i) as Chip
                filterModel.estateType?.forEach {
                    if (it == chip.text) { chip.isChecked = true }
                }
            }
            for (i in 0 until chipGroupPois.childCount) {
                val chip = chipGroupPois.getChildAt(i) as Chip
                filterModel.estatePois ?.forEach {
                    if (it == chip.text) { chip.isChecked = true }
                }
            }
        }
    }

    private fun configureSeekbars(view: View) {
        seekbarPrice = view.findViewById(R.id.seekbar_price)
        seekbarSurface = view.findViewById(R.id.seekbar_surface)
        tvMinPrice = view.findViewById(R.id.textview_filter_min_price)
        tvMaxPrice = view.findViewById(R.id.textview_filter_max_price)
        tvMinSurface = view.findViewById(R.id.textview_filter_min_surface)
        tvMaxSurface = view.findViewById(R.id.textview_filter_max_surface)

        estateViewModel.estateByPrice().observe(viewLifecycleOwner, Observer {
            seekbarPrice.setMaxValue(it[0].price.toFloat())
            seekbarPrice.setOnRangeSeekbarChangeListener { minValue, maxValue ->
                tvMinPrice.text = minValue.toString().toThousand()
                tvMaxPrice.text = maxValue.toString().toThousand()
            }
        })

        estateViewModel.estateBySurface().observe(viewLifecycleOwner, Observer {
            seekbarSurface.setMaxValue(it[0].surface.toFloat())
            seekbarSurface.setOnRangeSeekbarChangeListener { minValue, maxValue ->
                tvMinSurface.text = minValue.toString().toSquare()
                tvMaxSurface.text = maxValue.toString().toSquare()
            }
        })
    }

    private fun configureButtonsClicks(view: View) {
        btnOk = view.findViewById(R.id.textView_btnOk)
        btnCancel = view.findViewById(R.id.textView_cancel)
        btnResetAll = view.findViewById(R.id.textView_restalAll)
        editTextRooms = view.findViewById(R.id.edittext_filter_rooms)
        editTextLocality = view.findViewById(R.id.edittext_filter_locality)
        cbIsSold = view.findViewById(R.id.checkbox_isSold)
        cbPhotos = view.findViewById(R.id.checkbox_photos)

        btnOk.setOnClickListener {
            val filterRooms = if (!editTextRooms.text.isNullOrEmpty()) editTextRooms.text.toString().toInt() else 0
            val filterLocality = if (!editTextLocality.text.isNullOrEmpty()) editTextLocality.text.toString() else ""
            val minPrice = seekbarPrice.selectedMinValue.toInt()
            val maxPrice = seekbarPrice.selectedMaxValue.toInt()
            val minSurface = seekbarSurface.selectedMinValue.toInt()
            val maxSurface = seekbarSurface.selectedMaxValue.toInt()

            val filter = FilterModel(minPrice, maxPrice, minSurface, maxSurface,
                filterRooms, getSelectedChips(chipGroupTypes), getSelectedChips(chipGroupPois),
                filterLocality, cbIsSold.isChecked, cbPhotos.isChecked)

            Log.d("Filter", "Locality: $filterLocality")

            filterInteraction.setFilter(filter)

            dialog?.dismiss()
        }

        btnCancel.setOnClickListener {
            Log.d("FilterFragment", "Clicked on: Cancel")
            dialog?.dismiss()
        }

        btnResetAll.setOnClickListener {
            Log.d("FilterFragment", "Clicked on: Reset all")
            filterInteraction.setFilter(null)

            dialog?.dismiss()
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
}
