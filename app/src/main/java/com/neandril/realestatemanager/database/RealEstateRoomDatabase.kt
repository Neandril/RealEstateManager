package com.neandril.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.gms.maps.model.LatLng
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.utils.Converters
import com.neandril.realestatemanager.utils.toSquare
import com.neandril.realestatemanager.utils.toThousand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.icu.lang.UCharacter.GraphemeClusterBreak.V





@Database(entities = [Estate::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RealEstateRoomDatabase : RoomDatabase() {

    abstract fun estateDao(): EstateDao

    companion object {
        @Volatile
        private var INSTANCE: RealEstateRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ) : RealEstateRoomDatabase {

            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RealEstateRoomDatabase::class.java,
                    "estate_manager_database"
                )
                    .addCallback(EstateDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class EstateDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.estateDao())
                }
            }
        }

        suspend fun populateDatabase(estateDao: EstateDao) {
            // Delete all content here.
            estateDao.deleteAll()

            val BRISBANE = LatLng(-27.47093, 153.0235)
            val MELBOURNE = LatLng(-37.81319, 144.96298)

            // Add sample estate.
            var estate = Estate(1,
                "This is an address",
                "-27.47093, 153.0235",
                "Apartment",
                "120000".toThousand(),
                "150".toSquare(),
                "3",
                "2",
                "5",
                "Agent1",
                true,
                "2020-02-02",
                "",
                null)
            estateDao.insert(estate)

            estate = Estate(2,
                "And an another address",
                "-37.81319, 144.96298",
                "Manor",
                "3585000".toThousand(),
                "1125".toSquare(),
                "7",
                "5",
                "8",
                "Agent2",
                false,
                "",
                "",
                null)
            estateDao.insert(estate)
        }
    }
}