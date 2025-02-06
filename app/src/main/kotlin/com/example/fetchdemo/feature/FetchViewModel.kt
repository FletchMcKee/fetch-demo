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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

  private val _uiEvent = MutableSharedFlow<FetchUiEvent>(extraBufferCapacity = 20)
  val uiEvent = _uiEvent.asSharedFlow()

  fun fetchNetwork() {
    viewModelScope.launch(dispatchers.io) {
      fetchRepository.getFetchData()
        .onSuccess { Timber.d("fetchNetwork - success") }
        .onFailure {
          Timber.e(it, "fetchNetwork - error: ${it.message}")
          if (!_uiEvent.tryEmit(ShowSnackbarMessage("A network error occurred."))) {
            error("Buffer overflow error.")
          }
        }
    }
  }

  private fun mainUiState(): Flow<FetchUiState> = fetchRepository.selectFetchData()
    .asResult()
    .map { result ->
      when (result) {
        FetchResult.Loading -> FetchUiState.Loading
        is FetchResult.Error -> FetchUiState.Error(result.exception)
        is FetchResult.Success -> {
          if (result.data.isEmpty()) {
            FetchUiState.Empty
          } else {
            FetchUiState.Success(result.data.groupBy { it.listId })
          }
        }
      }
    }
}

sealed class FetchUiState {
  data object Loading : FetchUiState()
  data object Empty : FetchUiState()
  data class Success(val fetchDataMap: Map<Long, List<FetchData>>) : FetchUiState()
  data class Error(val error: Throwable) : FetchUiState()
}

sealed interface FetchUiEvent
data class ShowSnackbarMessage(val message: String) : FetchUiEvent
