package com.roko.kronos.util

import org.junit.Assert.assertEquals
import org.junit.Test

internal class ConvertersTest {

    @Test
    fun testToHarmonisedNetworkTimeAndDeltaMillis() {
        (37291L to 28865L).apply {
            assertEquals(this.toHarmonisedNetworkTimeAndDeltaMillis(precisionDrop = 2), 28891L to -8400L)
            assertEquals(this.toHarmonisedNetworkTimeAndDeltaMillis(precisionDrop = 3), 29291L to -8000L)
        }
        (17315L to 18972L).apply {
            assertEquals(this.toHarmonisedNetworkTimeAndDeltaMillis(precisionDrop = 2), 19015L to 1700L)
            assertEquals(this.toHarmonisedNetworkTimeAndDeltaMillis(precisionDrop = 3), 19315L to 2000L)
        }
        assertEquals((12345L to 12345L).toHarmonisedNetworkTimeAndDeltaMillis(precisionDrop = 1), 12345L to 0L)
        listOf((12345L to 67891L), (67891L to 12345L)).forEach {
            assertEquals(it.toHarmonisedNetworkTimeAndDeltaMillis(precisionDrop = 0), it.second to (it.second - it.first))
        }
    }

}
