package com.neandril.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.neandril.realestatemanager.models.Estate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Estate::class], version = 1, exportSchema = false)
public abstract class RealEstateRoomDatabase : RoomDatabase() {

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
                    "word_database"
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

            // Add sample words.
            var estate = Estate("Hello", "120.000")
            estateDao.insert(estate)
            estate = Estate("World!", "150.000")
            estateDao.insert(estate)

            // TODO: Add your own words!
        }
    }
}