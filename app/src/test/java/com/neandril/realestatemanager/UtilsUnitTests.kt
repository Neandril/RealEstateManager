package com.neandril.realestatemanager

import com.neandril.realestatemanager.utils.Utils
import com.neandril.realestatemanager.utils.toSquare
import com.neandril.realestatemanager.utils.toThousand
import junit.framework.Assert.assertEquals
import org.junit.Test

class UtilsUnitTests {

    /**
     * Convert Dollars to Euros
     */
    @Test
    fun should_Dollars_ConvertedTo_Euros() {
        // Convert some values to Euros
        val converted = Utils.convertDollarToEuro(100)
        val converted2 = Utils.convertDollarToEuro(1500)
        val converted3 = Utils.convertDollarToEuro(12585585)

        assertEquals(92, converted)
        assertEquals(1380, converted2)
        assertEquals(11578738, converted3)
    }

    /**
     * Convert Euros to Dollars
     */
    @Test
    fun should_Euros_ConvertedTo_Dollars() {
        // Convert some values to Dollars
        val converted = Utils.convertEuroToDollar(100)
        val converted2 = Utils.convertEuroToDollar(1500)
        val converted3 = Utils.convertEuroToDollar(12585585)

        assertEquals(108, converted)
        assertEquals(1620, converted2)
        assertEquals(13592432, converted3)
    }

    /**
     * Test .toSquare() extension
     */
    @Test
    fun toSquare() {
        val value = "150".toSquare()
        assertEquals("150 m²", value)
    }

    /**
     * Test .toThousand() extension
     */
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