package com.example.fetchdemo.rules

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.use
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.fetch.Database
import com.example.fetch.FetchListDbQueries
import io.mockk.every
import io.mockk.spyk
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class DatabaseTestRule : TestRule {
  lateinit var driver: SqlDriver
  lateinit var databaseSpy: Database
  lateinit var fetchListDbQueriesSpy: FetchListDbQueries
  val server = MockWebServer()

  val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(server.url("/"))
    .addConverterFactory(
      Json.asConverterFactory("application/json; charset=UTF8".toMediaType()),
    )
    .build()

  override fun apply(base: Statement, description: Description) = object : Statement() {
    override fun evaluate() {
      driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
      Database.Schema.create(driver)
      setUpSpies(driver)
      driver.use {
        base.evaluate()
      }
    }
  }

  private fun setUpSpies(driver: SqlDriver) {
    databaseSpy = spyk(Database(driver))
    fetchListDbQueriesSpy = spyk(databaseSpy.fetchListDbQueries)
    every { databaseSpy.fetchListDbQueries } returns fetchListDbQueriesSpy
  }
}
