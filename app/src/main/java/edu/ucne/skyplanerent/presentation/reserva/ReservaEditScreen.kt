package edu.ucne.skyplanerent.presentation.reserva

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel

@Composable
fun ReservaEditScreen(
    reservaId: Int,
    viewModel: ReservaViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    aeronaveViewModel: AeronaveViewModel = hiltViewModel(),
    goBack: (Int) -> Unit
) {
    LaunchedEffect(reservaId) {
        viewModel.selectReserva(reservaId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()
    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val aeronaveUiState by aeronaveViewModel.uiState.collectAsStateWithLifecycle()

    ReservaEditBodyScreen(
        uiState = uiState,
        onChangePasajeros = viewModel::onChangePasajeros,
        save = viewModel::getReserva,
        goBack = goBack,
        tipoVueloUiState = tipoVueloUiState,
        rutaUiState = rutaUiState,
        aeronaveUiState = aeronaveUiState
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaEditBodyScreen(
    uiState: UiState,
    onChangePasajeros: (Int) -> Unit,
    save: () -> Unit,
    goBack: (Int) -> Unit,
    tipoVueloUiState: TipoVueloUiState,
    rutaUiState: RutaUiState,
    aeronaveUiState: AeronaveUiState
) {

    val tipoVuelo = tipoVueloUiState.tipovuelo.find { it.tipoVueloId == uiState.tipoVueloId }
    val ruta = rutaUiState.rutas.find { it.rutaId == uiState.rutaId }
    val aeronave = aeronaveUiState.aeronaves.find { it.aeronaveId == uiState.categoriaId }

    val fecha = uiState.fecha
    val tipoCliente = uiState.tipoCliente
    val licencia = uiState.licenciaPiloto

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Modificar reserva",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                )
        },
        bottomBar = {
            Button(
                onClick = {
                    save()
                    goBack(0)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00F5A0),
                    contentColor = Color.Black
                )
            ) {
                Text("Confirmar cambios", fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Detalles de la reserva", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            InfoRow("Tipo de vuelo", tipoVuelo?.nombreVuelo ?: "No disponible")
            InfoRow("Aeronave", aeronave?.modeloAvion ?: "No disponible")
            InfoRow("Origen", ruta?.origen ?: "No disponible")
            InfoRow("Destino", ruta?.destino ?: "No disponible")
            //InfoRow("Duración", ruta?.duracion ?: "No disponible")
            InfoRow("Pasajeros", uiState.pasajeros.toString())
            InfoRow("Fecha", fecha?.toString() ?: "No seleccionada")
            InfoRow("Piloto", when (tipoCliente) {
                true -> "Sí"
                false -> "No"
                else -> "No especificado"
            })
            InfoRow("Licencia", licencia?.descripcion ?: "No aplica")

            Spacer(modifier = Modifier.height(24.dp))

            Text("Modificación", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            ChangeRow("Aeronaves") { /* Acción cambiar aeronave */ }
            ChangeRow("Fecha y hora") { /* Acción cambiar fecha */ }
            ChangeRow("Pasajeros") { /* Acción cambiar pasajeros */ }
            ChangeRow("Ruta") { /* Acción cambiar ruta */ }

            uiState.successMessage?.let { SuccessCard(it) }
            uiState.errorMessage?.let { ErrorCard(it) }
        }
    }
}


@Composable
fun InfoRow(title: String, value: String, onChange: () -> Unit = {}) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun ChangeRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 16.sp)
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(50)
        ) {
            Text("Cambiar")
        }
    }
}

@Composable
fun SuccessCard(message: String) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFFDFFFE0)
        )
    ) {
        Text(
            text = message,
            color = Color(0xFF007E33),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ErrorCard(message: String) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFFFFE0E0)
        )
    ) {
        Text(
            text = message,
            color = Color(0xFFB00020),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}
