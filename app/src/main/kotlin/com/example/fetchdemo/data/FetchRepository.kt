package com.example.fetchdemo.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.fetch.Database
import com.example.fetchdemo.util.suspendRunCatching
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class FetchRepository @Inject constructor(
  database: Database,
  private val dispatchers: DispatcherProvider,
  private val fetchApi: FetchApi,
) : FetchService {
  private val fetchListDbQueries = database.fetchListDbQueries

  override suspend fun getFetchData(): Result<Unit> = suspendRunCatching {
    val fetchList = fetchApi.getFetchData()
    upsertFetchData(fetchList)
  }

  override fun selectFetchData(): Flow<List<FetchData>> = fetchListDbQueries
    .selectSortedList(mapper = ::FetchData)
    .asFlow()
    .mapToList(dispatchers.io)

  // Don't expose database insert/update/delete commands. Keep them private and expose the network calls instead.
  private fun upsertFetchData(fetchList: List<FetchData>) {
    fetchListDbQueries.transaction {
      fetchList.forEach { item ->
        fetchListDbQueries.upsertFetchItem(
          id = item.id,
          list_id = item.listId,
          name = item.name,
        )
      }
    }
  }
}
