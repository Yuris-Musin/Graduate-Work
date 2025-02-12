package ru.musindev.graduate_work.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.musindev.graduate_work.data.local.request.SearchRequest
import ru.musindev.graduate_work.data.local.dao.SearchRequestDao

@Database(entities = [SearchRequest::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchRequestDao(): SearchRequestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "search_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
