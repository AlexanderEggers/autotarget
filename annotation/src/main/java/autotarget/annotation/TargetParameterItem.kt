package autotarget.annotation

import kotlin.reflect.KClass

/**
 * This annotation is part of the [TargetParameter] annotation value-list. This annotation is used
 * to define single parameter for the relevant target (activity/fragment).
 *
 * @property key [String] that acts as a key for the bundle value which is parsed to the target.
 * @property name [String] that used to improve the generated code. The name is re-used for all
 * the different generated classes and methods. That will help identifying parameters (if many are
 * given for a specific target).
 * @property type [KClass] that defines the type for this bundle value.
 * @property group [Array] of group-ids that defines in which groups the given parameter is going
 * to be used in. The grouping feature is useful if a specific target defines a lot of different
 * input parameter. Grouping is only rearranging optional parameter. Required parameter are always
 * part of any target related method.
 *
 * Example: There are five parameter defined (P1-P5) for the target x. P1 is a required parameter,
 * the other parameter are optional. Each parameter has their own type and alters the target in a
 * certain way. The grouping feature allows to define cases of:
 *
 * - P1, P3 and P4 could be a group.
 * - P1, P2 could be another one.
 * - ...
 *
 * @property required value that defines if the given parameter is required or not. A required
 * parameter is always part of any target related method/group. It indicates that parameter p is
 * always required by the target.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TargetParameterItem(
        val key: String,
        val name: String = "",
        val type: KClass<*>,
        val group: Array<String> = [],
        val required: Boolean)