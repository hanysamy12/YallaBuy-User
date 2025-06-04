package com.example.yallabuy_user.settings.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_cache")
    data class CurrencyRateCacheEntity(
        @PrimaryKey
        val id: Int = 1, //because we want to cache just one equation per day

        @ColumnInfo(name = "rates_json")
        val ratesJson: String,

        @ColumnInfo(name = "last_fetch_timestamp_millis")
        val lastFetchTimestampMillis: Long
    )

