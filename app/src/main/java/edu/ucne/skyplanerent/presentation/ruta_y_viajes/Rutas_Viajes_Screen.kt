package edu.ucne.skyplanerent.presentation.ruta_y_viajes

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.presentation.reserva.ReservaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel


@Composable
fun Rutas_Viajes_Screen(
    reservaViewModel: ReservaViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    aeronaveViewModel: AeronaveViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
   // rutas: List<RutaDTO>,
    scope: CoroutineScope,
    onCreate: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    goBackDetails: (Int) -> Unit
) {
   // val reservaUiState by reservaViewModel.uiState.collectAsStateWithLifecycle()
    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()
    val aeronavesUiState by aeronaveViewModel.uiState.collectAsStateWithLifecycle()




    Vuelos_RutasBodyListScreen(
        uiState = rutaUiState,
        uiStateA = aeronavesUiState,
        tiposDeVuelo = tipoVueloUiState.tipovuelo,
        // rutas = rutas,
        scope = scope,
        onCreate = onCreate,
        onEdit = onEdit,
        onDelete = onDelete,
        onReserva = { fecha ->
            val reserva = ReservaEntity(
                tipoVueloId = 1,
                rutaId = 1,
                fecha = fecha,
                pasajeros = 1,
                impuesto = 0.0,
                tarifa = 1000.0,
                precioTotal = 0.0
            )
            reservaViewModel.saveReserva()
        },
         goBackDetails = goBackDetails
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Vuelos_RutasBodyListScreen(
    uiState: RutaUiState,
    uiStateA: AeronaveUiState,
    tiposDeVuelo: List<TipoVueloDTO>,
    scope: CoroutineScope,
    onCreate: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onReserva: (Date) -> Unit,
    goBackDetails: (Int) -> Unit
) {
    var fechaSeleccionada by remember { mutableStateOf<Date?>(null) }
    val navController = rememberNavController()
    var selectedAeronave by remember { mutableStateOf<AeronaveDTO?>(null) }

    LaunchedEffect(uiStateA.aeronaves) {
        println("Aeronaves disponibles: ${uiStateA.aeronaves.size}")
        uiStateA.aeronaves.forEach {
            println("Aeronave: ${it.modeloAvion}")
        }
    }





    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tipos de vuelos y destinos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                FechaSelector(
                    fechaSeleccionada = fechaSeleccionada,
                    onFechaSeleccionada = { nuevaFecha -> fechaSeleccionada = nuevaFecha }
                )
            }

            item {
                Button(
                    onClick = { fechaSeleccionada?.let { onReserva(it) } },
                    enabled = fechaSeleccionada != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reservar con fecha")
                }
            }

            item {
                ListaDeTiposDeVuelo(tiposDeVuelo = tiposDeVuelo)
            }

            item {
                Text(
                    text = "Rutas disponibles",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            itemsIndexed(uiState.rutas) { index, ruta ->
                RutaListItem(
                    index = index + 1,
                    ruta = ruta,
                    onClick = { ruta.rutaId?.let { navController.navigate("ruta_detalles/$it") } }
                )
            }

            item {
                AeronaveDropdown(
                    aeronaves = uiStateA.aeronaves,
                    selectedAeronave = selectedAeronave,
                    onAeronaveSelected = { selectedAeronave = it }
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AeronaveDropdown(
    aeronaves: List<AeronaveDTO>,
    selectedAeronave: AeronaveDTO?,
    onAeronaveSelected: (AeronaveDTO) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = selectedAeronave?.modeloAvion ?: "Seleccionar aeronave",
            onValueChange = {},
            readOnly = true,
            label = { Text("Modelo de Aeronave") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (aeronaves.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No hay aeronaves disponibles") },
                    onClick = {}
                )
            }

            aeronaves.forEach { aeronave ->
                DropdownMenuItem(
                    text = { Text(aeronave.modeloAvion) },
                    onClick = {
                        onAeronaveSelected(aeronave)
                        expanded = false
                    }
                )
            }
        }

    }
}



@Composable
fun ListaDeTiposDeVuelo(tiposDeVuelo: List<TipoVueloDTO>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Tipos de vuelo",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tiposDeVuelo) { vuelo ->
                TipoVueloCard(vuelo)
            }
        }
    }
}

@Composable
fun TipoVueloCard(vuelo: TipoVueloDTO) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = vuelo.nombreVuelo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}


@Composable
fun ListaDeRutas(
    rutas: List<RutaDTO>,
    onRutaClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Rutas disponibles",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(rutas) { index, ruta ->
                RutaListItem(
                    index = index + 1,
                    ruta = ruta,
                    onClick = { onRutaClick(ruta.rutaId!!) }
                )
            }
        }
    }
}

@Composable
fun RutaListItem(
    index: Int,
    ruta: RutaDTO,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Ruta $index",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Origen: ${ruta.origen}, Destino: ${ruta.destino}",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = ">",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }
        Divider()
    }
}


@Composable
fun FechaSelector(
    fechaSeleccionada: Date?,
    onFechaSeleccionada: (Date) -> Unit
) {
    val contexto = LocalContext.current
    val calendario = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        contexto,
        { _, year, month, day ->
            val nuevaFecha = Calendar.getInstance()
            nuevaFecha.set(year, month, day)
            onFechaSeleccionada(nuevaFecha.time)
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Fecha seleccionada: ${
                fechaSeleccionada?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                } ?: "No seleccionada"
            }",
            style = MaterialTheme.typography.bodyLarge
        )

        Button(onClick = { datePickerDialog.show() }) {
            Text("Seleccionar fecha del vuelo")
        }
    }

}
