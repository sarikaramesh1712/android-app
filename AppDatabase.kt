package com.example.mobileproject.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [SignalIni::class, LocalSignal::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun signalDao(): SignalDao
    abstract fun localSignalDao(): LocalSignalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "signal_database"
                )
                    .addMigrations(MIGRATION_1_2) // Add migration
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create local_signals table
                database.execSQL("""
            CREATE TABLE local_signals (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                x INTEGER NOT NULL,
                y INTEGER NOT NULL,
                s1 INTEGER NOT NULL,
                s2 INTEGER NOT NULL,
                s3 INTEGER NOT NULL
            )
        """)
            }
        }
    }
}