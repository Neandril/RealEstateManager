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

    @Query("""SELECT * FROM real_estate_table WHERE price BETWEEN :minPrice AND :maxPrice AND surface BETWEEN :minSurface AND :maxSurface AND nbTotalRooms > :nbRooms AND type LIKE :type AND points_of_interest LIKE :points_of_interest AND address LIKE :location""")
    fun getFiltered(minPrice: Int, maxPrice: Int,
                    minSurface: Int, maxSurface: Int,
                    nbRooms: Int, type: String, points_of_interest: String, location: String): LiveData<List<Estate>>

/*    @Query("SELECT * FROM real_estate_table WHERE price = (SELECT MAX(price) FROM real_estate_table)")
    suspend fun getMaxPrice(): Int*/
}