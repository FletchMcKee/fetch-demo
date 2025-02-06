package com.example.fetchdemo.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchdemo.data.DispatcherProvider
import com.example.fetchdemo.data.FetchData
import com.example.fetchdemo.data.FetchRepository
import com.example.fetchdemo.util.FetchResult
import com.example.fetchdemo.util.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class FetchViewModel @Inject constructor(
  private val dispatchers: DispatcherProvider,
  private val fetchRepository: FetchRepository,
) : ViewModel() {

  val uiState: StateFlow<FetchUiState> = mainUiState()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
      initialValue = FetchUiState.Loading,
    )

  fun fetchNetwork() {
    viewModelScope.launch(dispatchers.io) {
      fetchRepository.getFetchData()
        .onSuccess { Timber.d("fetchNetwork - success") }
        .onFailure { Timber.e(it, "fetchNetwork - error: ${it.message}") }
    }
  }

  private fun mainUiState(): Flow<FetchUiState> = fetchRepository.selectFetchData()
    .asResult()
    .map { result ->
      when (result) {
        FetchResult.Loading -> FetchUiState.Loading
        is FetchResult.Error -> FetchUiState.Error(result.exception)
        is FetchResult.Success -> {
          FetchUiState.Success(result.data.groupBy { it.listId })
        }
      }
    }
}

sealed class FetchUiState {
  data object Loading : FetchUiState()
  data class Success(val fetchDataMap: Map<Long, List<FetchData>>) : FetchUiState()
  data class Error(val error: Throwable) : FetchUiState()
}
