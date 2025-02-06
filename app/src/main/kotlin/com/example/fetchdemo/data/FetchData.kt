package com.example.fetchdemo.data

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class FetchData(
  val id: Long,
  val listId: Long,
  val name: String?,
)
