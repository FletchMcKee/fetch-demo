package com.example.fetchdemo.data

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isSuccess
import com.example.fetch.FetchListDbQueries
import com.example.fetchdemo.rules.DatabaseTestRule
import com.example.fetchdemo.util.enqueueFromFile
import com.example.fetchdemo.util.enqueueHttpBadRequest
import io.mockk.verify
import io.mockk.verifyOrder
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.create

class FetchRepositoryTest {
  @get:Rule
  val dataRule = DatabaseTestRule()

  private val testContext = EmptyCoroutineContext
  private val dispatchers = DispatcherProvider(
    main = testContext,
    io = testContext,
    default = testContext,
    unconfined = testContext,
  )
  private lateinit var fetchRepository: FetchRepository
  private lateinit var fetchListDbQueriesSpy: FetchListDbQueries

  @Before
  fun setUp() {
    fetchRepository = FetchRepository(
      database = dataRule.databaseSpy,
      dispatchers = dispatchers,
      fetchApi = dataRule.retrofit.create<FetchApi>(),
    )
    // Makes the verifications more terse.
    fetchListDbQueriesSpy = dataRule.fetchListDbQueriesSpy
  }

  @After
  fun cleanUp() {
    dataRule.server.shutdown()
  }

  @Test
  fun `getFetchData - valid response returns isSuccess and upserts data`() = runTest {
    dataRule.server.enqueueFromFile(FETCH_SUCCESS)
    val result = fetchRepository.getFetchData()

    assertThat(result).isSuccess()
    verifyOrder {
      fetchListDbQueriesSpy.upsertFetchItem(id = 755L, list_id = 2L, name = "")
      fetchListDbQueriesSpy.upsertFetchItem(id = 684L, list_id = 1L, name = "Item 684")
      fetchListDbQueriesSpy.upsertFetchItem(id = 276L, list_id = 1L, name = "Item 276")
      fetchListDbQueriesSpy.upsertFetchItem(id = 190L, list_id = 2L, name = "Item 190")
    }
  }

  @Test
  fun `getFetchData - error response returns isFailure and doesn't upsert`() = runTest {
    dataRule.server.enqueueHttpBadRequest()
    val result = fetchRepository.getFetchData()

    assertThat(result).isFailure()
    assertThat(result.exceptionOrNull()!!).isInstanceOf(HttpException::class)
    verify(exactly = 0) { fetchListDbQueriesSpy.upsertFetchItem(any(), any(), any()) }
  }

  @Test
  fun `selectFetchData - valid response flow emits expected list`() = runTest {
    dataRule.server.enqueueFromFile(FETCH_SUCCESS)
    fetchRepository.getFetchData()

    fetchRepository.selectFetchData().test {
      assertThat(awaitItem()).isEqualTo(expectedFetchSuccess)

      expectNoEvents()
    }
  }

  @Test
  fun `selectFetchData - error response flow emits empty list`() = runTest {
    dataRule.server.enqueueHttpBadRequest()
    fetchRepository.getFetchData()

    fetchRepository.selectFetchData().test {
      assertThat(awaitItem()).isEmpty()
      expectNoEvents()
    }
  }

  private companion object {
    const val FETCH_SUCCESS = "fetch-success.json"
    val expectedFetchSuccess = listOf(
      FetchData(id = 276, listId = 1, name = "Item 276"),
      FetchData(id = 684, listId = 1, name = "Item 684"),
      FetchData(id = 190, listId = 2, name = "Item 190"),
    )
  }
}
