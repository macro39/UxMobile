package sk.uxtweak.uxmobile.core

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class SimpleCondition {
    private var deferred = CompletableDeferred<Unit>()

    suspend fun block() = deferred.await()

    fun open() {
        deferred = CompletableDeferred()
    }
}
