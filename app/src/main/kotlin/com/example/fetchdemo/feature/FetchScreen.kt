@file:OptIn(ExperimentalFoundationApi::class)

package com.example.fetchdemo.feature

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FetchScreen(
  viewModel: FetchViewModel = hiltViewModel(),
  modifier: Modifier = Modifier,
) {
  val fetchUiState: FetchUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel) {
    viewModel.fetchNetwork()
  }

  FetchList(fetchUiState, modifier)
}

@Composable
private fun FetchList(
  fetchUiState: FetchUiState,
  modifier: Modifier,
) {

  when (fetchUiState) {
    FetchUiState.Loading -> Unit
    is FetchUiState.Error -> Unit
    is FetchUiState.Success -> {
      LazyColumn(modifier = modifier.fillMaxSize()) {
        fetchUiState.fetchDataMap.forEach { (key, list) ->
          stickyHeader(key = key) {
            Text(
              text = "List ID: $key",
              modifier = Modifier.fillMaxWidth()
                .padding(8.dp)
            )
          }

          items(
            items = list,
            key = { fetchData ->
              fetchData.id
            }
          ) { fetchData ->
            Text(
              text = "ID: ${fetchData.id} name: ${fetchData.name}",
              modifier = Modifier.padding(8.dp)
            )
          }
        }
      }
    }
  }
}
