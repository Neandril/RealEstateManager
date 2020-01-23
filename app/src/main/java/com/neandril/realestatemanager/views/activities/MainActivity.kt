package com.neandril.realestatemanager.views.activities

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.views.base.BaseActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar

    override fun getActivityLayout(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar_common)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_add) {
            Toast.makeText(this, "Add Clicked", Toast.LENGTH_LONG).show()
            val intent = Intent(this, CreateRealEstate::class.java)
            startActivity(intent)
        }
        if (id == R.id.action_search) {
            Toast.makeText(this, "Search Clicked", Toast.LENGTH_LONG).show()
        }

        return super.onOptionsItemSelected(item)
    }
}
