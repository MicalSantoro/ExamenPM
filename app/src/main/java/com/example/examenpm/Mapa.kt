package com.example.examenpm


import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.examenpm.db.AppDb
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaUI(appVM: AppVM, variablesVM: VariablesVM, permissionLauncher: ActivityResultLauncher<Array<String>>) {
    val routineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var longText by remember { mutableStateOf("") }
    var latText by remember { mutableStateOf("") }

    val longitud = longText.toDoubleOrNull() ?: 0.0
    val latitud = latText.toDoubleOrNull() ?: 0.0

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){


        permissionLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION))
        OutlinedTextField(
            value = longText,
            onValueChange = {
                if (it.matches(Regex("")) || it.isEmpty()) {
                    longText = it
                }
            },
            label = { Text(text = "Longitud") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number).copy(imeAction = ImeAction.Done)
        )

        OutlinedTextField(
            value = latText,
            onValueChange = {
                if (it.matches(Regex("")) || it.isEmpty()) {
                    latText = it
                }
            },
            label = { Text(text = "Latitud") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number).copy(imeAction = ImeAction.Done)
        )

        Button(onClick = {

            routineScope.launch(Dispatchers.IO) {
                AppDb.getInstace(context).EntidadesDao().actualizarLocacion(variablesVM.id.value, latitud, longitud)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Lugar agregado", Toast.LENGTH_SHORT).show()
                }
            }

        }) {
            Text(text = "Agregar coordenadas")
        }
        Text(text = "Latitud: $latitud " +
                "Longitud: $longitud")
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { appVM.pantallaActual.value = Pantallas.MAIN}) {
            Text(text = "Volver")
        }
        Spacer(modifier = Modifier.height(200.dp))
        AndroidView(factory = {
            MapView(it).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                org.osmdroid.config.Configuration.getInstance().userAgentValue = context.packageName
                controller.setZoom(15.0)
            }
        }, update = {

            it.overlays.removeIf { true }
            it.invalidate()
            val geoPoint = GeoPoint(latitud, longitud)
            it.controller.animateTo(geoPoint)

            val marcador = Marker(it)
            marcador.position = geoPoint
            marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            it.overlays.add(marcador)

        })
    }
}





fun getLocation(context: Context, onSuccess: (location: Location?) -> Unit) {
    try {
        val service = LocationServices.getFusedLocationProviderClient(context)
        val task = service.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        )
        task.addOnSuccessListener { location ->
            Log.d("Location", "Location retrieved: $location")
            onSuccess(location)
        }
        task.addOnFailureListener { exception ->
            Log.e("Location", "Failed to retrieve location: $exception")
            onSuccess(null)
        }
    } catch (e: SecurityException) {
        Log.e("Location", "Failed to retrieve location: $e")
        onSuccess(null)
    }
}