package com.neandril.realestatemanager.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neandril.realestatemanager.R
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.viewmodels.EstateViewModel
import com.neandril.realestatemanager.views.MainRecyclerViewAdapter
import com.neandril.realestatemanager.views.base.BaseActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val newEstateActivityRequestCode = 1
    private lateinit var toolbar: Toolbar
    private lateinit var estateViewModel: EstateViewModel

    override fun getActivityLayout(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar_common)
        setSupportActionBar(toolbar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = MainRecyclerViewAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        estateViewModel = ViewModelProvider(this).get(EstateViewModel::class.java)
        estateViewModel.allEstates.observe(this, Observer { estates ->
            estates?.let { adapter.setEstate(it) }
        })

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
            startActivityForResult(intent, newEstateActivityRequestCode)
        }
        if (id == R.id.action_search) {
            Toast.makeText(this, "Search Clicked", Toast.LENGTH_LONG).show()
        }

        return super.onOptionsItemSelected(item)
    }

/*    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newEstateActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.getStringExtra(CreateRealEstate.EXTRA_REPLY)?.let {
                val estate = Estate(it)
                estateViewModel.insert(estate)
                Unit
            }
        } else {
            Toast.makeText(
                applicationContext,
                "not saved",
                Toast.LENGTH_LONG
            ).show()
        }
    }*/
}
