package edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaScreen(
    rutaId: Int? = null,
    viewModel: RutaViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { // Cambiado de rutaId a Unit
        if (uiState.rutaId == null && uiState.origen.isNullOrBlank() && uiState.destino.isNullOrBlank()) {
            if (rutaId != null && rutaId > 0) {
                viewModel.onEvent(RutaEvent.GetRuta(rutaId))
            } else {
                viewModel.onEvent(RutaEvent.New)
            }
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
    val showDialog = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Validaciones
    val origenError = uiState.origen.isNullOrBlank()
    val destinoError = uiState.destino.isNullOrBlank()
    val distanciaError = uiState.distancia <= 0.0
    val duracionError = uiState.duracionEstimada <= 0
    val isFormValid = !origenError && !destinoError && !distanciaError && !duracionError

    // Diálogo de éxito
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                onEvent(RutaEvent.New) // Reiniciar el estado después de cerrar el diálogo
            },
            title = {
                Text(
                    text = "Ruta Guardada Correctamente",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Ruta Guardada Correctamente",
                        tint = Color.Blue,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        onEvent(RutaEvent.New) // Reiniciar el estado después de cerrar el diálogo
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text("OK")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Manejo de eventos de UI
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateUp -> goBack()
                is UiEvent.ShowSnackbar -> {
                    scope.launch {
                        // No mostramos Snackbar, pero mantenemos la lógica por compatibilidad
                    }
                }
            }
        }
    }

    // Mostrar diálogo para éxito o Snackbar para error
    LaunchedEffect(uiState.isSuccess, uiState.errorMessage) {
        if (uiState.isSuccess && !uiState.successMessage.isNullOrBlank()) {
            showDialog.value = true
            onEvent(RutaEvent.ResetSuccessMessage)
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
                    containerColor = Color.Red.copy(alpha = 0.8f)
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
                .verticalScroll(rememberScrollState()) // Agregado para desplazamiento
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
                        onValueChange = {
                            onEvent(
                                RutaEvent.DistanciaChange(
                                    it.toDoubleOrNull() ?: 0.0
                                )
                            )
                        },
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
                        onValueChange = {
                            onEvent(
                                RutaEvent.DuracionEstimadaChange(
                                    it.toIntOrNull() ?: 0
                                )
                            )
                        },
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
                                        onEvent(RutaEvent.PostRuta)
                                    } else {
                                        onEvent(RutaEvent.Save)
                                    }
                                }
                            },
                            enabled = isFormValid,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isFormValid) Color.Blue else Color.Gray,
                                disabledContentColor = Color.Gray
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isFormValid) Color.Blue else Color.Gray
                            ),
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