package sk.uxtweak.uxmobile.rpc

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RpcParameter(val name: String)
