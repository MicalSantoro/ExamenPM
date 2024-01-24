package com.example.examenpm.db

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Entidades(
    @PrimaryKey val id: Int,
    @ColumnInfo var lugar: String,
    @ColumnInfo var imagen: Bitmap?,
    @ColumnInfo var latitud: Double?,
    @ColumnInfo var longitud: Double?,
    @ColumnInfo var orden: Int,
    @ColumnInfo var alojamiento: Double,
    @ColumnInfo var traslado: Double,
    @ColumnInfo var comentarios: String
)
