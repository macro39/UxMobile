package sk.uxtweak.uxmobile

import sk.uxtweak.uxmobile.core.toHumanUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class HumanUnitTest {
    @Test
    fun `Test human conversion`() {
        assertEquals("1 B", 1.toHumanUnit())
        assertEquals("1023 B", 1023.toHumanUnit())
        assertEquals("1 KB", 1024.toHumanUnit())
        assertEquals("1023.88 KB", ((1024 * 1024) - 128).toHumanUnit())
        assertEquals("1 MB", (1024 * 1024).toHumanUnit())
        assertEquals("1.5 MB", ((1024 * 1024) + 1024 * 512).toHumanUnit())
        assertEquals("1 PB", 1125899906842624.toHumanUnit())
        assertEquals("200 PB", (1125899906842624 * 200).toHumanUnit())
        assertEquals("1 EB", (1125899906842624 * 1024).toHumanUnit())
    }
}
