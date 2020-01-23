package com.neandril.realestatemanager.views.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity


abstract class BaseActivity: AppCompatActivity() {

    companion object {
        private val TAG = BaseActivity::class.java.simpleName
    }

    protected abstract fun getActivityLayout(): Int

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: BaseActivity")

        this.setContentView(getActivityLayout())
    }
}