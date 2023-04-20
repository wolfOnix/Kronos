package com.roko.kronos.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** Shorthand for the `Flow.stateIn(<scope>, SharingStarted.WhileSubscribed(), <initialValue>)` method with non-nullable result. */
fun <T> Flow<T>.asStateFlowIn(
    scope: CoroutineScope,
    initialValue: T
): StateFlow<T> = this.stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(), // update this variable only when UI is subscribed to this variable
    initialValue = initialValue
)

/** Shorthand for the `Flow.stateIn(<scope>, SharingStarted.WhileSubscribed(), <initialValue>)` method with nullable result. */
fun <T> Flow<T>.asNullableStateFlowIn(
    scope: CoroutineScope,
    initialValue: T? = null
): StateFlow<T?> = this.stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(), // update this variable only when UI is subscribed to this variable
    initialValue = initialValue
)
