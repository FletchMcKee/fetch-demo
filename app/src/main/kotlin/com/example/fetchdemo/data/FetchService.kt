package com.example.fetchdemo.data

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface FetchService {
  suspend fun getFetchData(): Result<Unit>

  fun selectFetchData(): Flow<List<FetchData>>
}

interface FetchApi {
  @GET("hiring.json")
  suspend fun getFetchData(): List<FetchData>
}


