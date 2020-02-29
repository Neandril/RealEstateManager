package com.neandril.realestatemanager.views.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.utils.Utils
import com.neandril.realestatemanager.views.adapters.MyListAdapter
import com.neandril.realestatemanager.views.base.BaseActivity
import com.neandril.realestatemanager.views.fragments.FilterFragment
import com.neandril.realestatemanager.views.fragments.FragmentMain

class MainActivity : BaseActivity() {

    private val newEstateActivityRequestCode = 1
    private lateinit var toolbar: Toolbar

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var fab: FloatingActionButton
    private lateinit var listview: ListView


    // ***************************
    // BASE METHODS
    // ***************************

    override fun getActivityLayout(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar = findViewById(R.id.toolbar_common)
        setSupportActionBar(toolbar)

        fab = findViewById(R.id.fab)

        this.configureDrawer()
        this.configureListView()
        this.configureFabClick()

        val path = getDatabasePath("estate_manager_database").absolutePath
        Log.d("Path", "path: $path")

        showFragment(FragmentMain())

    }

    // ***************************
    // UI SETUP
    // ***************************
    private fun configureDrawer() {
        drawer = findViewById(R.id.drawer_layout)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
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
        }

        return super.onOptionsItemSelected(item)
    }

    // Custom drawer menu configuration
    private fun configureListView() {
        listview = findViewById(R.id.list_menu_items)
        //
        val listItems = arrayOf(
            resources.getString(R.string.menu_map_view),
            resources.getString(R.string.menu_loan_simulator),
            resources.getString(R.string.menu_currency_converter))
        val listIcons = arrayOf(
            R.drawable.ic_map_view,
            R.drawable.ic_loan_simulator,
            R.drawable.ic_euros)

        val myListAdapter = MyListAdapter(this, listItems, listIcons)
        listview.adapter = myListAdapter

        listview.setOnItemClickListener(){adapterView, _, position, _ ->

            when (adapterView.getItemIdAtPosition(position).toInt()) {
                0 -> {
                    val intent = Intent(this, MapViewActivity::class.java)
                    startActivity(intent)
                }
                1 -> Toast.makeText(this, "Click on Loan", Toast.LENGTH_LONG).show()
                2 -> showConverterDialog()
            }

            drawer.closeDrawer(GravityCompat.START)
        }
    }

    private fun configureFabClick() {
        fab.setOnClickListener {

            val fm = supportFragmentManager
            fm.beginTransaction().add(FilterFragment(), "modal").commit()

        }
    }

    private fun showFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
    }

    private fun launchCreateNewEstateActivity() {
        val intent = Intent(this, CreateRealEstateActivity::class.java)
        startActivityForResult(intent, newEstateActivityRequestCode)
    }

    private fun showConverterDialog() {
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val builder = AlertDialog.Builder(this)


        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_currency_converter, viewGroup, false)
        val editTextDollars = dialogView.findViewById<EditText>(R.id.edittext_dollars)
        val editTextEuros = dialogView.findViewById<EditText>(R.id.edittext_euros)

        editTextDollars.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editTextDollars.hasFocus() && !editTextDollars.text.isNullOrEmpty()) {
                    editTextEuros.setText(
                        Utils.convertDollarToEuro(
                            s.toString().toInt()
                        ).toString())
                }
            }
        })

        editTextEuros.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editTextEuros.hasFocus() && !editTextEuros.text.isNullOrEmpty()) {
                    editTextDollars.setText(
                        Utils.convertEuroToDollar(
                            s.toString().toInt()
                        ).toString())
                }
            }
        })

        builder.setView(dialogView)
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.dialog_ok)
        { _, _ -> }
        builder.show()
    }
}
