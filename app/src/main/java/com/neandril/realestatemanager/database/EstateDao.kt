package com.neandril.realestatemanager.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.models.FilterModel

@Dao
interface EstateDao {

    @Query("SELECT * from real_estate_table ORDER BY id ASC")
    suspend fun getAllEstates(): List<Estate>

    @Query("SELECT * FROM real_estate_table WHERE id=:id")
    fun getSingleEstate(id: Int): LiveData<Estate>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(estate: Estate)

    @Query("DELETE FROM real_estate_table")
    suspend fun deleteAll()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateEstate(estate: Estate)

    @RawQuery(observedEntities = [Estate::class])
    suspend fun getFiltered(query: SupportSQLiteQuery) : List<Estate>

    @Query("SELECT * FROM real_estate_table ORDER BY price DESC")
    fun getEstateByPrice(): LiveData<List<Estate>>

    @Query("SELECT * FROM real_estate_table ORDER BY surface DESC")
    fun getEstateBySurface(): LiveData<List<Estate>>
}