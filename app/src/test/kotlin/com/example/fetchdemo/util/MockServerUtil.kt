package com.example.fetchdemo.util

import java.io.File
import java.net.HttpURLConnection
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

const val BASE_PATH = "src/test/mock-responses/"

fun MockWebServer.enqueueFromFile(path: String) = enqueue(MockResponse().setBody(File(BASE_PATH + path).readText(Charsets.UTF_8)))

fun MockWebServer.enqueueHttpBadRequest() = enqueue(
  MockResponse()
    .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
    .setBody(
      """
        {
          "error": "unauthorized"
        }
      """.trimIndent(),
    ),
)
