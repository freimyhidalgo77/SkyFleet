package edu.ucne.skyplanerent.presentation.aeronave

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AeronaveDetailsScreen(
    aeronaveId: Int?,
    viewModel: AeronaveViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(aeronaveId) {
        aeronaveId?.let {
            if (it > 0) {
                viewModel.onEvent(AeronaveEvent.GetAeronave(it))
            }
        }
    }

    AeronaveDetailsBodyScreen(
        uiState = uiState,
        goBack = goBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AeronaveDetailsBodyScreen(
    uiState: AeronaveUiState,
    goBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles de la Aeronave",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF272D4D)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(8.dp)
        ) {
            uiState.aeronaves.firstOrNull()?.let { aeronave ->
                Column {
                    Text(
                        text = "Aeronave ${aeronave.aeronaveId ?: "N/A"}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Modelo: ${aeronave.modeloAvion ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Categoría: ${aeronave.descripcionCategoria ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Registración: ${aeronave.registracion ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Licencia: ${aeronave.licencia ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Costo por Hora: $${aeronave.costoXHora?.toString() ?: "0.0"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Peso: ${aeronave.peso?.toString() ?: "0.0"} kg",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Velocidad Máxima: ${aeronave.velocidadMaxima?.toString() ?: "0.0"} km/h",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Rango: ${aeronave.rango?.toString() ?: "0"} km",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Capacidad de Combustible: ${aeronave.capacidadCombustible?.toString() ?: "0"} L",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Consumo por Hora: ${aeronave.consumoXHora?.toString() ?: "0"} L/h",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Capacidad de Pasajeros: ${aeronave.capacidadPasajeros?.toString() ?: "0"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Altitud Máxima: ${aeronave.altitudMaxima?.toString() ?: "0"} m",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Motor: ${aeronave.descripcionMotor ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Descripción: ${aeronave.descripcionAeronave ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                }
            } ?: run {
                Text(
                    text = "No se encontraron detalles de la aeronave",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }

            uiState.errorMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}