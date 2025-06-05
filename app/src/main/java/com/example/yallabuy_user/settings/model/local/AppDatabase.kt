package com.example.yallabuy_user.settings.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [CurrencyRateCacheEntity::class], version = 1, exportSchema = false)
    abstract class AppDatabase : RoomDatabase() {

        abstract fun currencyDao(): CurrencyDao

        companion object {
            @Volatile
            private var INSTANCE: AppDatabase? = null

            fun getDatabase(context: Context): AppDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "yallabuy_database"
                    ).build()
                    INSTANCE = instance
                    instance
                }
            }
        }

    }

