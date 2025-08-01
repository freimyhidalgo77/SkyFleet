package edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.presentation.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaScreen(
    rutaId: Int? = null,
    viewModel: RutaViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(rutaId) {
        if (rutaId != null && rutaId > 0) {
            viewModel.onEvent(RutaEvent.GetRuta(rutaId))
        } else {
            viewModel.onEvent(RutaEvent.New) // Inicializar para nueva ruta
        }
    }

    RutaBodyScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        goBack = goBack,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaBodyScreen(
    uiState: RutaUiState,
    onEvent: (RutaEvent) -> Unit,
    goBack: () -> Unit,
    viewModel: RutaViewModel
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    // Validaciones
    val origenError = uiState.origen.isNullOrBlank()
    val destinoError = uiState.destino.isNullOrBlank()
    val distanciaError = uiState.distancia <= 0.0
    val duracionError = uiState.duracionEstimada <= 0
    val isFormValid = !origenError && !destinoError && !distanciaError && !duracionError

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

    // Mostrar Snackbar para éxito o error
    LaunchedEffect(uiState.isSuccess, uiState.errorMessage) {
        if (uiState.isSuccess && !uiState.successMessage.isNullOrBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = uiState.successMessage,
                    duration = SnackbarDuration.Short
                )
                onEvent(RutaEvent.ResetSuccessMessage)
            }
        } else if (!uiState.errorMessage.isNullOrBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = uiState.errorMessage,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (uiState.isSuccess) Color.Green.copy(alpha = 0.8f) else Color.Red.copy(alpha = 0.8f)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.rutaId == null) "Nueva Ruta" else "Editar Ruta",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(if (uiState.rutaId == null) "Nueva Ruta" else "Editar Ruta")

                    OutlinedTextField(
                        value = uiState.rutaId?.toString() ?: "Nuevo",
                        onValueChange = {},
                        label = { Text("ID Ruta") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.origen ?: "",
                        onValueChange = { onEvent(RutaEvent.OrigenChange(it)) },
                        label = { Text("Origen") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = origenError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (origenError) {
                        Text(
                            text = "El origen no puede estar vacío",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.destino ?: "",
                        onValueChange = { onEvent(RutaEvent.DestinoChange(it)) },
                        label = { Text("Destino") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = destinoError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (destinoError) {
                        Text(
                            text = "El destino no puede estar vacío",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.distancia.toString(),
                        onValueChange = { onEvent(RutaEvent.DistanciaChange(it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("Distancia") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = distanciaError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (distanciaError) {
                        Text(
                            text = "La distancia debe ser mayor a 0",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.duracionEstimada.toString(),
                        onValueChange = { onEvent(RutaEvent.DuracionEstimadaChange(it.toIntOrNull() ?: 0)) },
                        label = { Text("Duración Estimada") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = duracionError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (duracionError) {
                        Text(
                            text = "La duración estimada debe ser mayor a 0",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    uiState.errorMessage?.let {
                        Text(text = it, color = Color.Red)
                    }

                    Spacer(modifier = Modifier.padding(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onEvent(RutaEvent.New) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Blue
                            ),
                            border = BorderStroke(1.dp, Color.Blue),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "new button"
                            )
                            Text("Nuevo")
                        }
                        OutlinedButton(
                            onClick = {
                                if (isFormValid) {
                                    if (uiState.rutaId == null) {
                                        onEvent(RutaEvent.PostRuta) // Crear nueva ruta
                                    } else {
                                        onEvent(RutaEvent.Save) // Editar ruta existente
                                    }
                                }
                            },
                            enabled = isFormValid,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isFormValid) Color.Blue else Color.Gray,
                                disabledContentColor = Color.Gray
                            ),
                            border = BorderStroke(1.dp, if (isFormValid) Color.Blue else Color.Gray),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(text = if (uiState.rutaId == null) "Guardar" else "Actualizar")
                        }
                    }
                }
            }
        }
    }
}