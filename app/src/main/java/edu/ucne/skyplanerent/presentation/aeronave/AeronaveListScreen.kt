package edu.ucne.skyplanerent.presentation.aeronave

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO

@Composable
fun AeronaveListScreen(
    viewModel: AeronaveViewModel = hiltViewModel(),
    createAeronave: () -> Unit,
    goToAeronave: (Int) -> Unit,
    goBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AeronaveListBodyScreen(
        uiState = uiState,
        goToAeronave = { id -> goToAeronave(id) },
        onEvent = viewModel::onEvent,
        createAeronave = createAeronave,
        goBack = goBack
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AeronaveListBodyScreen(
    uiState: AeronaveUiState,
    goToAeronave: (Int) -> Unit,
    onEvent: (AeronaveEvent) -> Unit,
    createAeronave: () -> Unit,
    goBack: () -> Unit
) {
    val refreshing = uiState.isLoading
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { onEvent(AeronaveEvent.GetAeronaves) }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Aeronaves",
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
        floatingActionButton = {
            FloatingActionButton(onClick = createAeronave) {
                Icon(Icons.Filled.Add, "Agregar nueva")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.aeronaves.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay aeronaves disponibles en este momento",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        items(uiState.aeronaves) { aeronave ->
                            AeronaveRow(
                                it = aeronave,
                                goToAeronave = { goToAeronave(aeronave.aeronaveId ?: 0) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                if (!uiState.errorMessage.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun AeronaveRow(
    it: AeronaveDTO,
    goToAeronave: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "Aeronave ${it.aeronaveId ?: "N/A"}",
            color = Color.Black
        )
        Column(
            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = "Modelo: ${it.modeloAvion}, Categoría: ${it.descripcionCategoria}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Blue
            )
            Text(
                text = "Registración: ${it.registracion}, Licencia: ${it.licencia}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Blue
            )
            Text(
                text = "Costo: $${it.costoXHora?.toString() ?: "0.0"}, Peso: ${it.peso?.toString() ?: "0.0"} kg",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Blue
            )
            Text(
                text = "Velocidad Máx: ${it.velocidadMaxima?.toString() ?: "0.0"} km/h, Rango: ${it.rango} km",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Blue
            )
            Text(
                text = "Combustible: ${it.capacidadCombustible} L, Consumo: ${it.consumoXHora} L/h",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Blue
            )
            Text(
                text = "Pasajeros: ${it.capacidadPasajeros}, Altitud Máx: ${it.altitudMaxima} m",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Blue
            )
            Text(
                text = "Motor: ${it.descripcionMotor}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Blue
            )
            Text(
                text = "Descripción: ${it.descripcionAeronave}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Blue
            )
        }
        IconButton(onClick = goToAeronave) {
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Modificar/Eliminar",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
    HorizontalDivider()
}