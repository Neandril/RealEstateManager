package com.neandril.realestatemanager

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.Nullable

class AppInit: Application() {

    companion object {
        private val TAG: String = AppInit::class.java.simpleName
    }

    private var appContext: Context? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: dimen: ${resources.getBoolean(R.bool.isTablet)}")
        this.appContext = this.applicationContext
    }

    @Nullable
    fun getAppContext(): Context? {
        return appContext
    }
}