package autotarget.annotation

import kotlin.reflect.KClass

/**
 * This annotation is part of the [TargetParameter] annotation value-list. This annotation is used
 * to define single parameter for the relevant target (activity/fragment).
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TargetParameterItem(
        /**
         * Returns a [String] that acts as a key for the bundle which is parsed to the target.
         */
        val key: String,

        /**
         * Returns a [String] that used to improve the generated code. The name is re-used for all
         * different generated classes and methods. That will help identifying parameters (if many
         * are given for a specific target).
         */
        val name: String = "",

        /**
         * Returns a [KClass] that defines the type for this parameter.
         */
        val type: KClass<*>,

        /**
         * Returns a [Array] of groups that defines for which cases this parameter is going to be
         * used.
         *
         * Example: There could be five parameter defined (P1-P5). Each parameter has their own type and
         * alters the target in a certain way. The grouping feature allows to define cases of:
         *
         * - P1, P3 and P4 could be a group.
         * - P1, P2 could be another one.
         * - ...
         */
        val group: Array<String> = [],

        /**
         * Returns a [Boolean] defines if the given parameter is required or not. A required
         * parameter is always part of any target related method. It indicates that parameter p is
         * always required by the target.
         */
        val required: Boolean)