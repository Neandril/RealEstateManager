package com.neandril.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.neandril.realestatemanager.models.Estate
import com.neandril.realestatemanager.utils.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Estate::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RealEstateRoomDatabase : RoomDatabase() {

    abstract fun estateDao(): EstateDao
}

object DbFactory {

    fun getDatabase(
        context: Context,
        scope: CoroutineScope
    ) : RealEstateRoomDatabase {

        return Room.databaseBuilder(
            context.applicationContext,
            RealEstateRoomDatabase::class.java,
            "estate_manager_database"
        )
            .addCallback(EstateDatabaseCallback(context, scope))
            .build()
    }

    private class EstateDatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            getDatabase(context, scope).let { database ->
                scope.launch {
                    populateDatabase(database.estateDao())
                }
            }
        }

        suspend fun populateDatabase(estateDao: EstateDao) {
            // Delete all content here.
            // estateDao.deleteAll()

            // Add sample estate.
            val estate = Estate(100,
                "1 E 62ns St, New York, NY 10065, Etats-Unis",
                "40.765922, -73.971076",
                "Apartment",
                1580000,
                "150",
                "3",
                "2",
                "5",
                "Agent2",
                true,
                "2020/02/02",
                "",
                null,
                null,
                "Park, Zoo, School")
            estateDao.insert(estate)

/*            estate = Estate(2,
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
                null,
                null)
            estateDao.insert(estate)*/
        }
    }
}