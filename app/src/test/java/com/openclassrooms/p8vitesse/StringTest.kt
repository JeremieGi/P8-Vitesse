package com.openclassrooms.p8vitesse

import org.junit.Assert.assertEquals
import org.junit.Test

class StringTest {

    // With a normal String
    @Test
    fun test_capitalized_classic(){

        val s = "aaa"

        val sResult = s.capitalized()

        assertEquals("Aaa",sResult)

    }

    // With empty String
    @Test
    fun test_capitalized_EmptyString(){

        val s = ""

        val sResult = s.capitalized()

        assertEquals("",sResult)

    }
}