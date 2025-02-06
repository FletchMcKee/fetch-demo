package com.example.fetchdemo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeStyle

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val PurpleHaze = Color(0xCC6650a4)

val FetchHazeStyle: HazeStyle
  @Composable
  get() = HazeDefaults.style(
    backgroundColor = MaterialTheme.colorScheme.surface,
    blurRadius = 15.dp,
    noiseFactor = 0.1f,
  )
