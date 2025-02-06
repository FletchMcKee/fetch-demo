package com.example.fetchdemo.data

import kotlin.coroutines.CoroutineContext

// We inject CoroutineContext instead of CoroutineDispatcher as it gives us better control in
// testing environments.
data class DispatcherProvider(
  val main: CoroutineContext,
  val io: CoroutineContext,
  val default: CoroutineContext,
  val unconfined: CoroutineContext,
)
