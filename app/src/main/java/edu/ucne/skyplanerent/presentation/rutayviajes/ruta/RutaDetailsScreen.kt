package edu.ucne.skyplanerent.presentation.rutayviajes.ruta

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.presentation.UiEvent
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaScreenDetails(
    rutaId: Int?,
    viewModel: RutaViewModel = hiltViewModel(),
    goBack: () -> Unit,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(rutaId) {
        rutaId?.let {
            if (it > 0) {
                viewModel.onEvent(RutaEvent.GetRuta(it))
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

    RutaDetailsBodyScreen(
        uiState = uiState,
        goBack = goBack,
        onDelete = {
            viewModel.onEvent(RutaEvent.Delete)
            onDelete(rutaId ?: 0)
        },
        onEdit = { onEdit(rutaId ?: 0) },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaDetailsBodyScreen(
    uiState: RutaUiState,
    goBack: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val refreshing = uiState.isLoading // Usamos el estado de carga del UI
    var showDeleteDialog by remember { mutableStateOf(false) } // Estado para controlar el diálogo

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ruta ${uiState.rutaId ?: 0}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.rutaId != null) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Detalle de la ruta",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Origen y Destino
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Origen",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Blue
                            )
                            Spacer(modifier = Modifier.weight(0.5f))
                            Text(
                                text = "Destino",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Blue
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.origen,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.weight(0.5f))
                            Text(
                                text = uiState.destino,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // Distancia y Duración
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Distancia",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Blue
                            )
                            Spacer(modifier = Modifier.weight(0.5f))
                            Text(
                                text = "Duración",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Blue
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${uiState.distancia} km",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.weight(0.5f))
                            Text(
                                text = "${uiState.duracionEstimada} min",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // Botones centrados mucho más abajo
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 96.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onEdit,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Modificar Ruta", color = Color.White)
                            }
                            Button(
                                onClick = { showDeleteDialog = true }, // Muestra el diálogo al hacer clic
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Eliminar Ruta", color = Color.White)
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

            // Indicador de carga si está activo
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Diálogo de confirmación para eliminar
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(
                            text = "Confirmar eliminación",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                    },
                    text = {
                        Text(
                            text = "¿Seguro que quieres eliminar esta ruta?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDelete()
                                showDeleteDialog = false
                            }
                        ) {
                            Text("Confirmar", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false }
                        ) {
                            Text("Cancelar", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        }
    }
}