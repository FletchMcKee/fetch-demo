package com.example.fetchdemo.di

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.fetch.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
  @Provides
  @Singleton
  fun provideSqlDriver(
    @ApplicationContext context: Context,
  ): SqlDriver = AndroidSqliteDriver(
    schema = Database.Schema,
    context = context,
    name = "fetch-demo.db",
    callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
      override fun onConfigure(db: SupportSQLiteDatabase) {
        db.enableWriteAheadLogging()
        db.setForeignKeyConstraintsEnabled(true)
      }
    },
  )

  @Provides
  @Singleton
  fun provideDatabase(driver: SqlDriver): Database = Database(driver = driver)
}
