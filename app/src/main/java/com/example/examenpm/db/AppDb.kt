package com.example.examenpm.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Entidades::class], version = 1)
@TypeConverters(ConversorImg::class)
abstract class AppDb : RoomDatabase() {
    abstract fun EntidadesDao(): EntidadesDao

    companion object {
        @Volatile
        private var BASE_DATOS: AppDb? = null

        fun getInstace(context: Context): AppDb {

            return BASE_DATOS ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    "EntidadesBD.bd"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { BASE_DATOS = it}
            }
        }
    }
}
