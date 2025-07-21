package edu.ucne.skyplanerent.presentation.aeronave

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.presentation.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoAeronaveDetailsScreen (
    aeronaveId: Int?,
    viewModel: AeronaveViewModel = hiltViewModel(),
    goBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(aeronaveId) {
        aeronaveId?.let {
            if (it > 0) {
                viewModel.onEvent(AeronaveEvent.GetAeronave(it))
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateUp -> goBack()
                is UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    TipoAeronaveDetailsBodyScreen(
        uiState = uiState,
        goBack = goBack,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoAeronaveDetailsBodyScreen (
    uiState: AeronaveUiState,
    goBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val refreshing = uiState.isLoading // Usamos el estado de carga del UI

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
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (uiState.isSuccess) Color.Green.copy(alpha = 0.8f) else Color.Red.copy(alpha = 0.8f)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                item {
                    if (uiState.isLoading) {
                        // Mostrar indicador de carga mientras se obtienen los datos
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.AeronaveId != null) {
                        // Capacidad
                        Text(
                            text = "Capacidad",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Peso Máximo: ${uiState.Peso?.toString() ?: "0.0"} kg",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Text(
                            text = "Tipo: ${uiState.DescripcionCategoria ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Text(
                            text = "Rango: ${uiState.Rango?.toString() ?: "0"} km",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Especificaciones
                        Text(
                            text = "Especificaciones",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Motor: ${uiState.DescripcionMotor ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Text(
                            text = "Velocidad Máxima: ${uiState.VelocidadMaxima?.toString() ?: "0.0"} km/h",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Text(
                            text = "Altitud Máxima: ${uiState.AltitudMaxima?.toString() ?: "0"} m",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Información Operacional
                        Text(
                            text = "Información Operacional",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Modelo: ${uiState.ModeloAvion ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Text(
                            text = "Registración: ${uiState.Registracion ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Text(
                            text = "Licencia: ${uiState.Licencia ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Text(
                            text = "Costo por Hora: $${uiState.CostoXHora?.toString() ?: "0.0"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Text(
                            text = "Capacidad de Combustible: ${uiState.CapacidadCombustible?.toString() ?: "0"} L",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Text(
                            text = "Consumo por Hora: ${uiState.ConsumoXHora?.toString() ?: "0"} L/h",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Text(
                            text = "Capacidad de Pasajeros: ${uiState.CapacidadPasajeros?.toString() ?: "0"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Text(
                            text = "Descripción: ${uiState.DescripcionAeronave ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                       } else {
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

            // Indicador de carga si está activo
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}