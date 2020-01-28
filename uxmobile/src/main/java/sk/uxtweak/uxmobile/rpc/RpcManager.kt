package sk.uxtweak.uxmobile.rpc

import org.json.JSONObject
import sk.uxtweak.uxmobile.net.WebSocketClient
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.coroutines.Continuation

class IllegalParameterException(message: String) : RuntimeException(message)

class RpcManager(private val socket: WebSocketClient) {
    fun <C : Any> create(contract: Class<C>) = proxy(contract) { method, args ->
        val json = JSONObject()
        method.parameterAnnotations.flatten().forEachIndexed { index, annotation ->
            if (annotation is RpcParameter) {
                json.put(annotation.name, args[index])
            } else {
                throw IllegalParameterException("Parameter does not have a name")
            }
        }
        socket.emit(method.name, json.toString()).toString()
    }
}

private typealias SuspendInvoker = suspend (method: Method, arguments: List<Any?>) -> Any?

private interface SuspendFunction {
    suspend fun invoke(): Any?
}

private val SuspendRemover = SuspendFunction::class.java.methods[0]

@Suppress("UNCHECKED_CAST")
private fun <C : Any> proxy(contract: Class<C>, invoker: SuspendInvoker): C =
    Proxy.newProxyInstance(contract.classLoader, arrayOf(contract)) { _, method, arguments ->
        val continuation = arguments.last() as Continuation<*>
        val argumentsWithoutContinuation = arguments.take(arguments.size - 1)
        SuspendRemover.invoke(object : SuspendFunction {
            override suspend fun invoke() = invoker(method, argumentsWithoutContinuation)
        }, continuation)
    } as C
