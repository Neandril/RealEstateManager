package com.neandril.realestatemanager.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.neandril.realestatemanager.models.Estate

@Dao
interface EstateDao {

    @Query("SELECT * from real_estate_table ORDER BY address ASC")
    fun getAlphabetized(): LiveData<List<Estate>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(estate: Estate)

    @Query("DELETE FROM real_estate_table")
    suspend fun deleteAll()
}