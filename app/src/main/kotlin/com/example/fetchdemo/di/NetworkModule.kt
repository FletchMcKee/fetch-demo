package com.example.fetchdemo.di

import com.example.fetch.Database
import com.example.fetchdemo.data.DispatcherProvider
import com.example.fetchdemo.data.FetchApi
import com.example.fetchdemo.data.FetchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun provideRetrofit(): Retrofit = Retrofit.Builder()
    .baseUrl("https://fetch-hiring.s3.amazonaws.com/")
    .addConverterFactory(
      Json.asConverterFactory("application/json; charset=UTF8".toMediaType()),
    )
    .build()

  @Provides
  @Singleton
  fun provideFetchApi(retrofit: Retrofit): FetchApi = retrofit.create<FetchApi>()

  @Provides
  @Singleton
  fun provideFetchDispatchers(): DispatcherProvider = DispatcherProvider(
    main = Dispatchers.Main,
    io = Dispatchers.IO,
    default = Dispatchers.Default,
    unconfined = Dispatchers.Unconfined,
  )

  @Provides
  @Singleton
  fun provideFetchRepository(
    database: Database,
    dispatchers: DispatcherProvider,
    fetchApi: FetchApi,
  ): FetchRepository = FetchRepository(
    database = database,
    dispatchers = dispatchers,
    fetchApi = fetchApi,
  )
}
