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

@Composable
fun ReservaEditScreen(
    reservaId: Int,
    viewModel: ReservaViewModel = hiltViewModel(),
    goBack: (Int) -> Unit
) {
    LaunchedEffect(reservaId) {
        viewModel.selectReserva(reservaId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReservaEditBodyScreen(
        uiState = uiState,
        onChangePasajeros = viewModel::onChangePasajeros,
        save = viewModel::getReserva,
        goBack = goBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaEditBodyScreen(
    uiState: UiState,
    onChangePasajeros: (Int) -> Unit,
    save: () -> Unit,
    goBack: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Modificar reserva",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
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
            Text(
                text = "Detalles de la reserva",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            InfoRow(title = "Aeronave", value = "Cessna 172") {
                // Acción al cambiar aeronave
            }

           // InfoRow(title = "Hora y Fecha", value = uiState.fecha ?: "No definida") {
                // Acción al cambiar hora y fecha
            }

            InfoRow(title = "Pasajeros", value = uiState.pasajeros.toString()) {
                // Acción al cambiar pasajeros
            }

            InfoRow(title = "Origen", value = "San Francisco, CA") {
                // Acción al cambiar origen
            }

            InfoRow(title = "Destino", value = "Los Angeles, CA") {
                // Acción al cambiar destino
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Modificación",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            ChangeRow("Aeronaves") {
                // Acción cambiar aeronave
            }

            ChangeRow("Fecha y hora") {
                // Acción cambiar fecha
            }

            ChangeRow("Pasajeros") {
                // Acción cambiar pasajeros
            }

            ChangeRow("Ruta") {
                // Acción cambiar ruta
            }

            uiState.successMessage?.let { message ->
                SuccessCard(message)
            }

            uiState.errorMessage?.let { message ->
                ErrorCard(message)
            }
        }
    }
//}

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
