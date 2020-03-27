package sk.uxtweak.uxmobile.concurrency

interface Condition {
    fun open()
    suspend fun block()
}
