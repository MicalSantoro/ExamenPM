package com.example.examenpm.db


import android.graphics.Bitmap
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Conversor {

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return android.graphics.BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}
