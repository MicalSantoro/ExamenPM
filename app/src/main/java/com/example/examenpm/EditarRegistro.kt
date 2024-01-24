package com.example.examenpm


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.examenpm.db.AppDb
import com.example.examenpm.ws.Factory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ModifyPlace(appVM: AppVM, formVM: FormVM) {

    val routineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val service = Factory.getDolarService()
                val response = service.getDolar()
                Log.d("Respuesta de la API", response.toString())
                val jsonString = response.toString()
                formVM.dolar.value = response.serie[0].valor
                Log.d("JSON de respuesta", jsonString)
                Log.d("Valor del dolar", response.serie[0].valor.toString())
            } catch (e: Exception) {
                // Manejar el error
                Log.e("Error en la llamada API", e.toString())
            }
        }
    }

    var lugar by remember { mutableStateOf("") }
    var orden by remember { mutableStateOf("") }
    var alojamiento by remember { mutableStateOf("") }
    var traslado by remember { mutableStateOf("") }
    var comentarios by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "MODIFICAR LUGAR")
        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = "Lugar")

        OutlinedTextField(
            value = lugar ,
            onValueChange = { lugar = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )

        )

        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = "Orden")

        OutlinedTextField(
            value = orden,
            onValueChange = { orden = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number).copy(imeAction = ImeAction.Done)
        )


        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = "Alojamiento")

        OutlinedTextField(
            value = alojamiento,
            onValueChange = { alojamiento = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number).copy(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = "Costo Movilización")

        OutlinedTextField(
            value = traslado,
            onValueChange = { traslado = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number).copy(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = "Comentarios")

        OutlinedTextField(
            value = comentarios,
            onValueChange = { comentarios = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.padding(16.dp))

        Button(onClick = {
            routineScope.launch(Dispatchers.IO) {
                val dao = AppDb.getInstace(context).EntidadesDao()
                dao.actualizarLugar(
                    formVM.id.value,
                    lugar,
                    orden.toInt(),
                    BigDecimal(alojamiento.toDouble()/formVM.dolar.value).setScale(2, RoundingMode.HALF_EVEN).toDouble(),
                    BigDecimal(traslado.toDouble()/formVM.dolar.value).setScale(2, RoundingMode.HALF_EVEN).toDouble(),
                    comentarios)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Lugar modificado", Toast.LENGTH_SHORT).show()
                }

                lugar = ""
                orden = ""
                alojamiento = ""
                traslado = ""
                comentarios = ""

                appVM.currentScreen.value = Screen.MAIN
            }
        }) {
            Text(text = "Modificar")
        }
        Spacer(modifier =   Modifier.padding(16.dp))
        Button(onClick = { appVM.currentScreen.value = Screen.MAIN}) {
            Text(text = "Volver")
        }

    }
}