package com.neandril.realestatemanager.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.neandril.realestatemanager.models.Estate

@Dao
interface EstateDao {

    @Query("SELECT * from real_estate_table ORDER BY id ASC")
    fun getAllEstates(): LiveData<List<Estate>>

    @Query("SELECT * FROM real_estate_table WHERE id=:id")
    fun getSingleEstate(id: Int): LiveData<Estate>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(estate: Estate)

    @Query("DELETE FROM real_estate_table")
    suspend fun deleteAll()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateEstate(estate: Estate)

/*    @Query("SELECT * FROM real_estate_table WHERE price = (SELECT MAX(price) FROM real_estate_table)")
    fun getMaxPrice(): Int*/
}