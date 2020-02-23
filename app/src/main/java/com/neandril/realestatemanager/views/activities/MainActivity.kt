package com.neandril.realestatemanager.views.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.lifecycle.Observer
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.viewmodels.EstateViewModel
import com.neandril.realestatemanager.views.base.BaseActivity
import com.neandril.realestatemanager.views.fragments.FilterFragment
import com.neandril.realestatemanager.views.fragments.FragmentMain
import com.neandril.realestatemanager.views.fragments.FragmentMap
import kotlin.math.max

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val newEstateActivityRequestCode = 1
    private lateinit var toolbar: Toolbar

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var fab: FloatingActionButton

    private lateinit var estateViewModel: EstateViewModel
    private var maxValue: Float? = 0f

    // ***************************
    // BASE METHODS
    // ***************************

    override fun getActivityLayout(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar_common)
        setSupportActionBar(toolbar)

        fab = findViewById(R.id.fab)

        this.configureDrawer()
        this.configureFabClick()

        val path = getDatabasePath("estate_manager_database").absolutePath
        Log.d("Path", "path: $path")

        showFragment(FragmentMain())

    }

    // ***************************
    // UI SETUP
    // ***************************

    private fun showCustomView(dialogBehavior: DialogBehavior = ModalDialog) {
        val dialog = MaterialDialog(this, dialogBehavior).show {
            title(R.string.filter_title)
            cancelable(false)
            customView(R.layout.fragment_filter, scrollable = true, horizontalPadding = true)
            val chipGroupTypes = findViewById<ChipGroup>(R.id.chipGroup_type)
            val chipGroupPois = findViewById<ChipGroup>(R.id.chipGroup_pois)
            val typeList = resources.getStringArray(R.array.estate_type)
            val poisList = resources.getStringArray(R.array.point_of_interest)
            val seekbarPrice = findViewById<CrystalRangeSeekbar>(R.id.seekbar_price)
            val tvMaxPrice = findViewById<TextView>(R.id.textview_filter_max_price)

            for (type in typeList) {
                val chip = Chip(context)
                val chipDrawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Choice)
                chip.setChipDrawable(chipDrawable)
                chip.text = type
                chipGroupTypes.addView(chip)
            }

            for (poi in poisList) {
                val chip = Chip(context)
                val chipDrawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Filter)
                chip.setChipDrawable(chipDrawable)
                chip.text = poi
                chipGroupPois.addView(chip)
            }

            seekbarPrice.setMaxValue(140000000F)
            seekbarPrice.setOnRangeSeekbarChangeListener { _, maxValue ->
                tvMaxPrice.text = maxValue.toString()
            }



            positiveButton(R.string.dialog_ok) { dialog ->
                Log.d("CustomDialog", "OK")
            }
            negativeButton(R.string.dialog_ok)
        }

        dialog.show()
    }

    private fun getMaxPrice() {
        estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
        // val maxPrice = estateViewModel.getMaxPrice()
/*        estateViewModel.getMaxPrice().observe(this, Observer {
            maxValue = it.price.toFloat()
            Log.d("MaxPrice", "MaxPrice: " + it.price)
        })*/
    }

    /*private fun showFilterDialog() {
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.filter_view, viewGroup, false)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.filter_title)
        builder.setCancelable(false)

        val chipGroupTypes = dialogView.findViewById<ChipGroup>(R.id.chipGroup_type)
        val chipGroupPois = dialogView.findViewById<ChipGroup>(R.id.chipGroup_pois)
        val typeList = resources.getStringArray(R.array.estate_type)
        val poisList = resources.getStringArray(R.array.point_of_interest)

        for (type in typeList) {
            val chip = Chip(dialogView.context)
            val chipDrawable = ChipDrawable.createFromAttributes(this, null, 0, R.style.Widget_MaterialComponents_Chip_Choice)
            chip.setChipDrawable(chipDrawable)
            chip.text = type
            chipGroupTypes.addView(chip)
        }

        for (poi in poisList) {
            val chip = Chip(dialogView.context)
            val chipDrawable = ChipDrawable.createFromAttributes(this, null, 0, R.style.Widget_MaterialComponents_Chip_Filter)
            chip.setChipDrawable(chipDrawable)
            chip.text = poi
            chipGroupPois.addView(chip)
        }


        builder.setView(dialogView)
        builder.setPositiveButton(R.string.dialog_ok)
        { _, _ ->
            Toast.makeText(this, "OK", Toast.LENGTH_LONG).show()
        }

        builder.setNegativeButton("Cancel")
        { _, _ ->
            Toast.makeText(this, "Cancel", Toast.LENGTH_LONG).show()
        }

        builder.setNeutralButton("Reset all")
        { _, _ ->
            Toast.makeText(this, "Reset all", Toast.LENGTH_LONG).show()
        }

        builder.show()
    }*/

    private fun configureDrawer() {
        drawer = findViewById(R.id.drawer_layout)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }


    // ***************************
    // ACTION CLICKS EVENTS
    // ***************************
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_add -> this.launchCreateNewEstateActivity()
            R.id.action_search -> Toast.makeText(this, "Search Clicked", Toast.LENGTH_LONG).show()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_one -> showFragment(FragmentMap()) // Toast.makeText(this, "Item one", Toast.LENGTH_LONG).show()
            R.id.nav_item_two -> Toast.makeText(this, "Item two", Toast.LENGTH_LONG).show()
            R.id.nav_item_three -> Toast.makeText(this, "Item three", Toast.LENGTH_LONG).show()
        }
        drawer.closeDrawer(GravityCompat.START)

        return true
    }

    private fun configureFabClick() {
        fab.setOnClickListener {
            // showFilterDialog()
            showCustomView(BottomSheet(LayoutMode.WRAP_CONTENT))
/*            val fm = supportFragmentManager
            fm.beginTransaction().add(FilterFragment(), "modal").commit()*/

        }
    }

    private fun showFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
    }

    private fun launchCreateNewEstateActivity() {
        val intent = Intent(this, CreateRealEstate::class.java)
        startActivityForResult(intent, newEstateActivityRequestCode)
    }
}
