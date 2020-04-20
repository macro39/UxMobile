package sk.uxtweak.uxmobile

import org.junit.Test
import sk.uxtweak.uxmobile.core.toJson
import sk.uxtweak.uxmobile.persister.database.EventEntity
import sk.uxtweak.uxmobile.util.TAG
import sk.uxtweak.uxmobile.util.logd

class JsonTest {
    @Test
    fun testToJson() {
        val events = listOf(
            EventEntity(42, "69", "{at:123,type:12}"),
            EventEntity(44, "69", "{at:1234,type:13}"),
            EventEntity(70, "69", "{at:1235,type:14}")
        )
        val json = events.toJson("69", null, "6af0b42b-82c1-11ea-99da-7bdcba612609")
        logd(TAG, "Json: $json")
    }
}
