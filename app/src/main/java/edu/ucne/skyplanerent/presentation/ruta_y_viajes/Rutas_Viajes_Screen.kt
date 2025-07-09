package edu.ucne.skyplanerent.presentation.ruta_y_viajes

import android.app.DatePickerDialog
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.presentation.reserva.ReservaViewModel
import edu.ucne.skyplanerent.presentation.reserva.UiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Rutas_Viajes_Screen(
    reservaViewModel: ReservaViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
   // rutas: List<RutaDTO>,
    scope: CoroutineScope,
    onCreate: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
   // onRutaClick: (Int) -> Unit
) {
   // val reservaUiState by reservaViewModel.uiState.collectAsStateWithLifecycle()
    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()


    Vuelos_RutasBodyListScreen(
        uiState = rutaUiState,
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
      //  onRutaClick = onRutaClick
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Vuelos_RutasBodyListScreen(
    uiState: RutaUiState,
    tiposDeVuelo: List<TipoVueloDTO>,
   // rutas: List<RutaDTO>,
    scope: CoroutineScope,
    onCreate: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onReserva: (Date) -> Unit,
   // onRutaClick: (Int) -> Unit
) {
    var fechaSeleccionada by remember { mutableStateOf<Date?>(null) }
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tipos de vuelos y destinos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()
        ) {



            Spacer(modifier = Modifier.height(8.dp))

            // Selector de fecha
            FechaSelector(
                fechaSeleccionada = fechaSeleccionada,
                onFechaSeleccionada = { nuevaFecha ->
                    fechaSeleccionada = nuevaFecha
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    fechaSeleccionada?.let {
                        onReserva(it)
                    }
                },
                enabled = fechaSeleccionada != null
            ) {
                Text("Reservar con fecha")
            }

            Spacer(modifier = Modifier.height(16.dp))

            ListaDeTiposDeVuelo(tiposDeVuelo = tiposDeVuelo)

            Spacer(modifier = Modifier.height(16.dp))

            ListaDeRutas(
                rutas = uiState.rutas,
                onRutaClick = { rutaId ->
                    navController.navigate("ruta_detalles/$rutaId")
                }
            )


            Spacer(modifier = Modifier.height(16.dp))

          /*  LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.reservas) { reserva ->
                    Text("Reserva ID: ${reserva.reservaId} - Fecha: ${
                        reserva.fecha?.let {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                        } ?: ""
                    }")
                }
            }*/
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
                    onClick = { onRutaClick(ruta.RutaId!!) }
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
