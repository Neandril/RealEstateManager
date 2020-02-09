package com.neandril.realestatemanager.views.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.views.base.BaseActivity
import com.neandril.realestatemanager.views.fragments.FragmentMain
import com.neandril.realestatemanager.views.fragments.FragmentMap

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val newEstateActivityRequestCode = 1
    private lateinit var toolbar: Toolbar

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

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

        this.configureDrawer()

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
