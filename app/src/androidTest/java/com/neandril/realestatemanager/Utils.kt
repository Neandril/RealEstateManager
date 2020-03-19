package com.neandril.realestatemanager

import androidx.test.platform.app.InstrumentationRegistry
import com.neandril.realestatemanager.utils.Utils.isInternetAvailable
import junit.framework.Assert.assertTrue
import org.junit.Test


class Utils {

    @Test
    fun given_context_when_isInternetAvailable_then_check_result() {
        val context = InstrumentationRegistry.getInstrumentation().getContext()
        assertTrue(isInternetAvailable(context))
    }
}