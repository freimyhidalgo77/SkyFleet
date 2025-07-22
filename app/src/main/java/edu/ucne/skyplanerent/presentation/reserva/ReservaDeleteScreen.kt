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
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import java.util.Date


@Composable
fun ReservaDeleteScreen(
    reservaId: Int,
    viewModel: ReservaViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    goBack: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(reservaId) {
        println("🛠️ ID recibido: $reservaId")
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
                    goBack = goBack
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

        Text("📅 Fecha: ${reserva.fecha}", fontSize = 16.sp)
        Text("📍 Origen ID: ${reserva.rutaId}", fontSize = 16.sp)
        Text("✈️ Aeronave ID: ${reserva.categoriaId}", fontSize = 16.sp)

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
