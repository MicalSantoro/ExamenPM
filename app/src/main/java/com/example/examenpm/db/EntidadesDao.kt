package com.example.examenpm.db


import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface EntidadesDao {

    @Query("SELECT * From Entidades ORDER BY orden ASC")
    fun listarTodo(): List<Entidades>

    @Insert
    fun insertar(entidades: Entidades): Long

    @Update
    fun actualizar(entidades: Entidades)

    @Delete
    fun eliminar(entidades: Entidades)

    @Query("DELETE FROM Entidades")
    fun eliminarTodo()

    @Query("UPDATE Entidades SET imagen = :imagen WHERE id = :id")//actualizar imagen, hace una consulta dependiendo del id
    fun actualizarImagen(id: Int, imagen: Bitmap)

    @Query("UPDATE Entidades SET lugar = :lugar, orden = :orden, alojamiento = :alojamiento, traslado = :traslado, comentarios = :comentarios WHERE id = :id")
    fun actualizarLugar(
        id: Int,
        lugar: String,
        orden: Int,
        alojamiento: Double,
        traslado: Double,
        comentarios: String
    )

    @Query("UPDATE Entidades SET longitud = :longitud, latitud = :latitud WHERE id = :id")
    fun actualizarLocacion(id: Int, longitud: Double, latitud: Double)

}