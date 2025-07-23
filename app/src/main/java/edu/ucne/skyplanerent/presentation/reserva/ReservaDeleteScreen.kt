package edu.ucne.skyplanerent.presentation.reserva

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel
import java.util.Date


@Composable
fun ReservaDeleteScreen(
    reservaId: Int,
    viewModel: ReservaViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    aeronaveViewModel: AeronaveViewModel = hiltViewModel(),
    goBack: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()
    val aeronaveUiState by aeronaveViewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }


    val reserva = uiState.reservas.find { it.reservaId == reservaId }

    val ruta = rutaUiState.rutas.find { it.rutaId == reserva?.rutaId }
    val tipoVuelo = tipoVueloUiState.tipovuelo.find {it.tipoVueloId == reserva?.tipoVueloId}
    val aeronave = aeronaveUiState.aeronaves.find { it.aeronaveId == reserva?.categoriaId }

    LaunchedEffect(reservaId) {
        println(" ID recibido: $reservaId")
        viewModel.selectReserva(reservaId) // Esto actualiza el uiState con la reserva específica si implementaste bien selectReserva
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { /* ... */ }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            val reserva = uiState.reservaSeleccionada

            if (reserva != null) {
                ReservaDeleteRow(
                    reserva = reserva,
                    uiState = uiState,
                    onEliminarClick = { showDialog = true },
                    goBack = goBack,
                    ruta = ruta,
                    tipoVuelo = tipoVuelo,
                    aeronave = aeronave
                )
            } else {
                Text(
                    text = "Cargando reserva...",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirmar Eliminación") },
                text = { Text("¿Realmente deseas eliminar esta reserva? Esta acción no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteReserva()
                            showDialog = false
                            goBack(0)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}


@Composable
fun ReservaDeleteRow(
    reserva: ReservaEntity,
    ruta: RutaDTO?,
    tipoVuelo: TipoVueloDTO?,
    aeronave: AeronaveDTO?,
    uiState: UiState,
    onEliminarClick: () -> Unit,
    goBack: (Int) -> Unit
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "¿Estás seguro de que quieres cancelar esta reserva?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )

        Text(
            text = "Cancelar su reserva podría resultar en cargos por cancelación o afectar " +
                    "su elegibilidad para un reembolso. Por favor, revise los términos y condiciones antes de continuar.",
            fontSize = 16.sp,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Fecha: ${reserva.fecha}", fontSize = 16.sp)
        Text("Tipo de vuelo: ${ tipoVuelo?.nombreVuelo ?: "No disponible"}", fontSize = 16.sp)
        Text("Origen: ${ ruta?.origen ?: "No disponible"}", fontSize = 16.sp)
        Text("Destino: ${ ruta?.destino ?: "No disponible"}", fontSize = 16.sp)
        Text("Aeronave ID: ${aeronave?.modeloAvion }", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { goBack(0) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(text = "Cancelar")
            }

            Button(
                onClick = onEliminarClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Eliminar")
            }
        }

        uiState.errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
