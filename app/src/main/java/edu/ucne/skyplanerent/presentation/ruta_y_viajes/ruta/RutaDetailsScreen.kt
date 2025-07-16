package edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaDetailsScreen(
    rutaId: Int?,
    viewModel: RutaViewModel = hiltViewModel(),
    goBack: () -> Unit,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(rutaId) {
        rutaId?.let {
            if (it > 0) {
                viewModel.onEvent(RutaEvent.GetRuta(it))
            }
        }
    }

    RutaDetailsBodyScreen(
        uiState = uiState,
        goBack = goBack,
        onDelete = { onDelete(rutaId ?: 0) },
        onEdit = { onEdit(rutaId ?: 0) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaDetailsBodyScreen(
    uiState: RutaUiState,
    goBack: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles de la Ruta",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(8.dp)
        ) {
            item {
                if (uiState.rutaId != null) {
                    // Información General
                    Text(
                        text = "Información General",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ID Ruta: ${uiState.rutaId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Origen: ${uiState.origen}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Destino: ${uiState.destino}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Detalles Operacionales
                    Text(
                        text = "Detalles Operacionales",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Distancia: ${uiState.distancia} km",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Duración: ${uiState.duracionEstimada} h",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = onDelete,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Eliminar Ruta", color = Color.White)
                        }
                        Button(
                            onClick = onEdit,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Modificar Ruta", color = Color.White)
                        }
                    }
                } else {
                    Text(
                        text = "No se encontraron detalles de la ruta",
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
}