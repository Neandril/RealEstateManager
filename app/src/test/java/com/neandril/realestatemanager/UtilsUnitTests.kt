package com.neandril.realestatemanager

import com.neandril.realestatemanager.utils.toSquare
import com.neandril.realestatemanager.utils.toThousand
import junit.framework.Assert.assertEquals
import org.junit.Test

class UtilsUnitTests {

    @Test
    fun toSquare() {
        val value = "150".toSquare()
        assertEquals("150 m²", value)
    }

    @Test
    fun thousandsSeparator() {
        val price = "1500000".toThousand()
        val price2 = "12852098".toThousand()
        val price3 = "35000000".toThousand()

        assertEquals("1 500 000", price)
        assertEquals("12 852 098", price2)
        assertEquals("35 000 000", price3)
    }

}