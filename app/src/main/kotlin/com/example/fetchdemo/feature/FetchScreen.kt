@file:OptIn(
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class,
)

package com.example.fetchdemo.feature

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fetchdemo.data.FetchData
import com.example.fetchdemo.ui.theme.FetchDemoTheme
import com.example.fetchdemo.ui.theme.FetchHazeStyle
import com.example.fetchdemo.ui.theme.PurpleHaze
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource

@Composable
fun FetchScreen(
  modifier: Modifier = Modifier,
  viewModel: FetchViewModel = hiltViewModel(),
) {
  val fetchUiState: FetchUiState by viewModel.uiState.collectAsStateWithLifecycle()
  val hazeState = remember { HazeState() }

  LaunchedEffect(viewModel) {
    viewModel.fetchNetwork()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Fetch Demo",
            color = Color.White,
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(PurpleHaze),
        modifier = Modifier
          .fillMaxWidth(),
      )
    },
    modifier = modifier,
  ) { innerPadding ->
    FetchList(
      fetchUiState,
      hazeState,
      Modifier.padding(innerPadding),
    )
  }
}

@Composable
private fun FetchList(
  fetchUiState: FetchUiState,
  hazeState: HazeState,
  modifier: Modifier = Modifier,
) {
  when (fetchUiState) {
    FetchUiState.Loading -> {
      LinearProgressIndicator(
        color = PurpleHaze,
        modifier = modifier.fillMaxWidth(),
      )
    }
    is FetchUiState.Error -> {
      Text(
        text = "An error occurred. Please try again.",
        color = MaterialTheme.colorScheme.error,
        modifier = modifier.padding(16.dp),
      )
    }
    is FetchUiState.Success -> {
      LazyColumn(
        modifier = modifier.fillMaxSize(),
      ) {
        fetchUiState.fetchDataMap.forEach { (key, list) ->
          stickyHeader(key = "list_$key") {
            Text(
              text = "List ID: $key",
              style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
              ),
              modifier = Modifier.fillMaxWidth()
                .shadow(elevation = 1.dp)
                .hazeEffect(state = hazeState, style = FetchHazeStyle)
                .padding(16.dp),
            )
          }

          items(
            items = list,
            key = { fetchData ->
              fetchData.id
            },
          ) { fetchData ->
            Text(
              text = "Name: ${fetchData.name}",
              style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
              ),
              modifier = Modifier
                .fillMaxWidth()
                .hazeSource(state = hazeState)
                .padding(12.dp),
            )
          }
        }
      }
    }
  }
}

@Preview
@Composable
private fun FetchListLoadingPreview() {
  FetchDemoTheme {
    val hazeState = remember { HazeState() }
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("Fetch Demo", color = Color.White) },
          colors = TopAppBarDefaults.topAppBarColors(PurpleHaze),
        )
      },
    ) { innerPadding ->
      FetchList(
        fetchUiState = FetchUiState.Loading,
        hazeState = hazeState,
        modifier = Modifier.padding(innerPadding),
      )
    }
  }
}

@Preview
@Composable
private fun FetchListErrorPreview() {
  FetchDemoTheme {
    val hazeState = remember { HazeState() }
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("Fetch Demo", color = Color.White) },
          colors = TopAppBarDefaults.topAppBarColors(PurpleHaze),
        )
      },
    ) { innerPadding ->
      FetchList(
        fetchUiState = FetchUiState.Error(RuntimeException("Boom!")),
        hazeState = hazeState,
        modifier = Modifier.padding(innerPadding),
      )
    }
  }
}

@Preview
@Composable
private fun FetchListSuccessPreview() {
  FetchDemoTheme {
    val hazeState = remember { HazeState() }
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("Fetch Demo", color = Color.White) },
          colors = TopAppBarDefaults.topAppBarColors(PurpleHaze),
        )
      },
    ) { innerPadding ->
      FetchList(
        fetchUiState = FetchUiState.Success(
          fetchDataMap = mapOf(
            1L to listOf(
              FetchData(id = 1L, listId = 1L, name = "Item 1"),
              FetchData(id = 2L, listId = 1L, name = "Item 2"),
            ),
            2L to listOf(
              FetchData(id = 3L, listId = 2L, name = "Item 3"),
            ),
          ),
        ),
        hazeState = hazeState,
        modifier = Modifier.padding(innerPadding),
      )
    }
  }
}
