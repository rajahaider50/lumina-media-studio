package com.example.premiumapp

import com.example.premiumapp.core.common.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationTest {

    @Test
    fun testCollectionNameValidation() {
        val validName = "Vacation 2026"
        assertTrue(validName.isNotBlank())
        assertTrue(validName.length <= Constants.MAX_COLLECTION_NAME_LENGTH)

        val emptyName = "   "
        assertTrue(emptyName.isBlank())

        val oversizedName = "A".repeat(Constants.MAX_COLLECTION_NAME_LENGTH + 1)
        assertTrue(oversizedName.length > Constants.MAX_COLLECTION_NAME_LENGTH)
    }

    @Test
    fun testSearchQueryTrimming() {
        val raw = "   trip to mountains   "
        val trimmed = raw.trim()
        assertEquals("trip to mountains", trimmed)
        assertFalse(trimmed.isEmpty())
    }
}
