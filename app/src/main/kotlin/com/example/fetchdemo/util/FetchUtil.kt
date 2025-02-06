package com.example.fetchdemo.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
  throw cancellationException
} catch (exception: Exception) {
  Result.failure(exception)
}

sealed class FetchResult<out T> {
  data class Success<T>(val data: T) : FetchResult<T>()
  data class Error(val exception: Throwable) : FetchResult<Nothing>()
  data object Loading : FetchResult<Nothing>()
}

fun <T> Flow<T>.asResult(): Flow<FetchResult<T>> = map<T, FetchResult<T>> { FetchResult.Success(it) }
  .onStart { emit(FetchResult.Loading) }
  .catch { emit(FetchResult.Error(it)) }
