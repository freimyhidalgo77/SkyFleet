package edu.ucne.skyplanerent.presentation.ruta_y_viajes

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import edu.ucne.skyplanerent.presentation.reserva.ReservaViewModel
import edu.ucne.skyplanerent.presentation.reserva.UiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Rutas_Viajes_Screen(
    reservaViewModel: ReservaViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    scope: CoroutineScope,
    onCreate: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    val reservaUiState by reservaViewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()

    Vuelos_RutasBodyListScreen(
        uiState = reservaUiState,
        tiposDeVuelo = tipoVueloUiState.tipovuelo,
        scope = scope,
        onCreate = onCreate,
        onEdit = onEdit,
        onDelete = onDelete,
        onReserva = { fecha ->
            // Aquí puedes guardar la reserva usando el ViewModel
            val reserva = ReservaEntity(
                tipoVueloId = 1, // Por ejemplo: vuelo seleccionado
                rutaId = 1,      // ruta seleccionada
                fecha = fecha,
                pasajeros = 1,
                impuesto = 0.0,
                tarifa = 1000.0,
                precioTotal = 0.0
            )
            reservaViewModel.saveReserva()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Vuelos_RutasBodyListScreen(
    uiState: UiState,
    tiposDeVuelo: List<TipoVueloEntity>,
    scope: CoroutineScope,
    onCreate: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onReserva: (Date) -> Unit
) {
    var fechaSeleccionada by remember { mutableStateOf<Date?>(null) }

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

            LazyColumn(
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
            }
        }
    }
}

@Composable
fun ListaDeTiposDeVuelo(
    tiposDeVuelo: List<TipoVueloEntity>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Tipos de vuelo disponibles",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tiposDeVuelo) { vuelo ->
                TipoVueloCard(vuelo)
            }
        }
    }
}

@Composable
fun TipoVueloCard(vuelo: TipoVueloEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .width(220.dp)
            .padding(horizontal = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = vuelo.descripcionTipoVuelo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Precio: ${vuelo.precio}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
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
