package autotarget.annotation

import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TargetParameterItem(val key: String,
                                     val name: String = "unspecified",
                                     val type: KClass<*>,
                                     val group: Array<String> = [DEFAULT_GROUP_KEY]) {

    companion object {
        const val DEFAULT_GROUP_KEY = "default"
    }
}