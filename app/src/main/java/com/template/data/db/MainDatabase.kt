package com.template.data.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.template.data.db.model.LinkModel

@Database(entities = [LinkModel::class], version = 1, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {

    abstract fun getDao(): MainDao

    companion object {
        private val LOCK = Any()

        private var db_instance: MainDatabase? = null
        private const val DB_NAME = "my_main_database"

        fun newInstance(application: Application): MainDatabase {
            db_instance?.let {
                return it
            }

            synchronized(LOCK) {
                db_instance?.let {
                    return it
                }

                val newInstance =
                    Room.databaseBuilder(application, MainDatabase::class.java, DB_NAME)
                        .fallbackToDestructiveMigration()
                        .build()

                db_instance = newInstance
                return newInstance
            }
        }
    }
}