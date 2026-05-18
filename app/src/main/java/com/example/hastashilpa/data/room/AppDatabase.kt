package com.example.hastashilpa.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hastashilpa.data.model.MaterialLog

@Database(entities = [MaterialLog::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun materialLogDao(): MaterialLogDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hastashilpa_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}