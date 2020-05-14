package sk.uxtweak.uxmobile

import org.junit.jupiter.api.Test
import sk.uxtweak.uxmobile.util.getFieldExtended
import sk.uxtweak.uxmobile.util.getFieldInstance
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

data class ReflectionTestClass(val publicField: String, private val privateField: Int)

class ReflectionTest {
    @Test
    fun fieldTest() {
        val testObject = ReflectionTestClass("Reflection Test", 42)
        val publicField = testObject.getFieldInstance<String>("publicField")
        val privateField = testObject.getFieldInstance<Int>("privateField")
        assertEquals("Reflection Test", publicField)
        assertEquals(42, privateField)
    }

    @Test
    fun fieldExtendedTest() {
        val testObject = ReflectionTestClass("Reflection Extended Test", 99)
        val publicField = testObject::class.java.getFieldExtended("publicField")
        val privateField = testObject::class.java.getFieldExtended("privateField")
        assertEquals(publicField.name, "publicField")
        assertEquals(privateField.name, "privateField")
    }

    @Test
    fun fieldExtendedTestFail() {
        val testObject = ReflectionTestClass("Reflection Fail Test", 13)
        assertFailsWith<NoSuchFieldException> {
            testObject::class.java.getFieldExtended("nonExistentField")
        }
    }
}
