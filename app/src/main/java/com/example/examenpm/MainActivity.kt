package com.example.examenpm



import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Create
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.examenpm.db.AppDb
import com.example.examenpm.db.Entidades
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Screen {
    MAIN,
    CAMARA,
    MAPA,
    REGISTRO,
    EDITARREGISTRO,
   // VERREGISTRO
}
class AppVM: ViewModel() {
    val currentScreen = mutableStateOf(Screen.MAIN)
    val onCameraPermissionOk:() -> Unit = {}
    var locationPermissionOk:() -> Unit = {}
}

class FormVM: ViewModel() {
    val id = mutableStateOf(0)
    val placeVisited = mutableStateOf("")
    val photo = mutableStateOf<Bitmap?>(null)
    val lat = mutableStateOf(0.0);
    val lon = mutableStateOf(0.0);
    val order = mutableStateOf(0)
    val price = mutableStateOf(0.0)
    val movePrice = mutableStateOf(0.0)
    val comments = mutableStateOf("")
    val dolar = mutableStateOf(0.0)
}
class MainActivity : ComponentActivity() {


    private val appVM = AppVM()
    private val cameraVm = AppVM()

    private lateinit var cameraController: LifecycleCameraController

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        appVM.locationPermissionOk()
        if(it[android.Manifest.permission.CAMERA] == true) {
            cameraVm.onCameraPermissionOk()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        cameraController = LifecycleCameraController(this)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        setContent {
            AppUI(permissionLauncher = permissionLauncher, cameraController = cameraController, appVM = appVM, formVM = FormVM())
        }
    }
}


@Composable
fun AppUI(
    permissionLauncher: ActivityResultLauncher<Array<String>>,
    cameraController: LifecycleCameraController,
    appVM: AppVM,
    formVM: FormVM
) {

    when(appVM.currentScreen.value) {
        Screen.MAIN -> {
            listPlacesUI(appVM = appVM, formVM = formVM)
        }
        Screen.CAMARA -> {
            CamaraUI(permissionLauncher = permissionLauncher, cameraController = cameraController, appVM = appVM, formVM = formVM)
        }
        Screen.MAPA-> {
            MapUI(appVM = appVM, formVM = formVM, permissionLauncher = permissionLauncher)
        }
        Screen.REGISTRO -> {
            addPlace(formVM = formVM , appVM = appVM)
        }
        Screen.EDITARREGISTRO -> {
            ModifyPlace(formVM = formVM, appVM = appVM)
        }
    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun listPlacesUI(appVM: AppVM,
                 formVM: FormVM,
) {
    val (places, setplaces) = remember { mutableStateOf(emptyList<Entidades>())}
    val context = LocalContext.current
    val routineScope = rememberCoroutineScope()

    LaunchedEffect(places) {
        withContext(Dispatchers.IO) {
            val dao = AppDb.getInstace(context).EntidadesDao()
            setplaces(dao.listarTodo())
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(places) { place ->
            PlaceItem(place, appVM = appVM,formVM) {
                setplaces(emptyList<Entidades>())
            }

        }

    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment  = Alignment.BottomEnd
    ) {
        Button(onClick = { appVM.currentScreen.value = Screen.REGISTRO }) {
            Text(text = "Agregar Lugar")
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment  = Alignment.BottomStart
    ) {
        Button(onClick = {routineScope.launch(Dispatchers.IO) {
            val dao = AppDb.getInstace(context).EntidadesDao()
            dao.eliminarTodo()
            setplaces(emptyList())
        }}) {
            Text(text = "Borrar todo")
        }
    }

}



@Composable
fun PlaceItem(place: Entidades, appVM: AppVM,formVM: FormVM, onSave:() -> Unit = {}) {
    val routineScope = rememberCoroutineScope()
    val context = LocalContext.current


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        place.imagen?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "foto de lugar",
                modifier = Modifier
                    .size(150.dp)
                    .padding(end = 16.dp)
                    .clickable { appVM.currentScreen.value = Screen.CAMARA }
            )
        } ?: Image(
            painter = painterResource(id = R.drawable.camara),
            contentDescription = "foto de lugar",
            modifier = Modifier
                .size(150.dp)
                .padding(end = 16.dp)
                .clickable {
                    formVM.id.value = place.id
                    appVM.currentScreen.value = Screen.CAMARA
                }
        )
        Column(
            verticalArrangement = Arrangement.Bottom,

            ) {
            Text(text = place.lugar)
            Text(text = "Costo x Noche: $" + place.alojamiento.toString() + " USD")
            Text(text = "Traslado: $" + place.traslado.toString() + " USD")
            Text(text = "Lat: " + place.latitud.toString() + " Lon: " + place.longitud.toString())
            Row {
                Icon(Icons.TwoTone.Delete, contentDescription = "Delete", Modifier.clickable {
                    routineScope.launch(Dispatchers.IO) {
                        val dao = AppDb.getInstace(context).EntidadesDao()
                        dao.eliminar(place)
                        onSave()
                    }
                })
                Spacer(modifier = Modifier.size(10.dp))
                Icon(Icons.TwoTone.Create, contentDescription = "Modify", Modifier.clickable {
                    formVM.id.value = place.id
                    appVM.currentScreen.value = Screen.EDITARREGISTRO
                }
                )
                Spacer(modifier = Modifier.size(10.dp))
                Icon(Icons.TwoTone.Place, contentDescription = "Location", Modifier.clickable {
                    formVM.id.value = place.id
                    appVM.currentScreen.value = Screen.MAPA
                })
            }

        }
    }}
