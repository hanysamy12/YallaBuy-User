package com.example.yallabuy_user.settings.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrencyDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRates(cache: CurrencyRateCacheEntity)

    @Query("SELECT * FROM currency_cache WHERE id = 1")
    suspend fun getCachedRates(): CurrencyRateCacheEntity?

}
