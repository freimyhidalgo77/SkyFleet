package edu.ucne.skyplanerent.presentation.aeronave

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.skyplanerent.presentation.UiEvent
import kotlinx.coroutines.launch

@Composable
fun AeronaveDetailsScreen(
    aeronaveId: Int?,
    viewModel: AeronaveViewModel = hiltViewModel(),
    goBack: () -> Unit,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit
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

    AeronaveDetailsBodyScreen(
        uiState = uiState,
        goBack = goBack,
        onDelete = {
            viewModel.onEvent(AeronaveEvent.Delete)
            onDelete(aeronaveId ?: 0)
        },
        onEdit = { onEdit(aeronaveId ?: 0) },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AeronaveDetailsBodyScreen(
    uiState: AeronaveUiState,
    goBack: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val showDeleteDialog = remember { mutableStateOf(false) } // Estado para el diálogo de eliminación

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles de la Aeronave",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
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
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Imagen principal
                    when {
                        uiState.imageUrl != null -> {
                            AsyncImage(
                                model = uiState.imageUrl,
                                contentDescription = "Imagen de ${uiState.modeloAvion}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                                error = painterResource(id = android.R.drawable.ic_menu_gallery)
                            )
                        }
                        uiState.imageUri != null -> {
                            AsyncImage(
                                model = uiState.imageUri,
                                contentDescription = "Imagen de ${uiState.modeloAvion}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                                error = painterResource(id = android.R.drawable.ic_menu_gallery)
                            )
                        }
                        else -> {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }
                }

                item {
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.aeronaveId != null) {
                        // Especificaciones Clave
                        Text(
                            text = "Especificaciones Clave",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Capacidad", color = Color.Gray)
                                Text("${uiState.capacidadPasajeros} Personas", color = Color.Black)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Tipo", color = Color.Gray)
                                Text(uiState.descripcionCategoria, color = Color.Black)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Peso Máximo", color = Color.Gray)
                                Text("${uiState.peso ?: "0.0"} kg", color = Color.Black)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Rango", color = Color.Gray)
                                Text("${uiState.rango} NM", color = Color.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Especificaciones
                        Text(
                            text = "Especificaciones",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Motor", color = Color.Gray)
                                Text(uiState.descripcionMotor, color = Color.Black)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Combustible", color = Color.Gray)
                                Text("${uiState.capacidadCombustible} gal", color = Color.Black)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Velocidad de Crucero", color = Color.Gray)
                                Text("${uiState.velocidadMaxima ?: "0"} KTAS", color = Color.Black)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Altitud Máxima", color = Color.Gray)
                                Text("${uiState.altitudMaxima} m", color = Color.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Información Operacional
                        Text(
                            text = "Información Operacional",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column {
                            Text("Modelo", color = Color.Gray)
                            Text(uiState.modeloAvion, color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Registración", color = Color.Gray)
                            Text(uiState.registracion, color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Licencia", color = Color.Gray)
                            Text(uiState.licencia, color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Costo por Hora", color = Color.Gray)
                            Text("$${uiState.costoXHora ?: "0.0"}", color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Consumo por Hora", color = Color.Gray)
                            Text("${uiState.consumoXHora} L/h", color = Color.Black)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Descripción", color = Color.Gray)
                            Text(uiState.descripcionAeronave, color = Color.Black)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botones centrados verticalmente
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = onEdit,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Modificar Aeronave")
                            }
                            Button(
                                onClick = { showDeleteDialog.value = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Eliminar Aeronave")
                            }
                        }
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

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar la aeronave ${uiState.modeloAvion}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog.value = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog.value = false }
                ) {
                    Text("Rechazar")
                }
            }
        )
    }
}