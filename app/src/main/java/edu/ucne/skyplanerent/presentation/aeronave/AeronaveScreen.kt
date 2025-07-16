package edu.ucne.skyplanerent.presentation.aeronave

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
fun AeronaveScreen(
    aeronaveId: Int? = null,
    viewModel: AeronaveViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(aeronaveId) {
        if (aeronaveId != null && aeronaveId > 0) {
            viewModel.onEvent(AeronaveEvent.GetAeronave(aeronaveId))
        } else {
            viewModel.onEvent(AeronaveEvent.New) // Inicializar para nueva aeronave
        }
    }

    AeronaveBodyScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        goBack = goBack,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AeronaveBodyScreen(
    uiState: AeronaveUiState,
    onEvent: (AeronaveEvent) -> Unit,
    goBack: () -> Unit,
    viewModel: AeronaveViewModel
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    // Validaciones
    val estadoIdError = uiState.estadoId == null || uiState.estadoId <= 0
    val modeloAvionError = uiState.ModeloAvion.isBlank()
    val descripcionCategoriaError = uiState.DescripcionCategoria.isBlank()
    val registracionError = uiState.Registracion.isBlank()
    val costoXHoraError = uiState.CostoXHora == null || uiState.CostoXHora <= 0.0
    val descripcionAeronaveError = uiState.DescripcionAeronave.isBlank()
    val velocidadMaximaError = uiState.VelocidadMaxima == null || uiState.VelocidadMaxima <= 0.0
    val descripcionMotorError = uiState.DescripcionMotor.isBlank()
    val capacidadCombustibleError = uiState.CapacidadCombustible <= 0
    val consumoXHoraError = uiState.ConsumoXHora <= 0
    val pesoError = uiState.Peso == null || uiState.Peso <= 0.0
    val rangoError = uiState.Rango <= 0
    val capacidadPasajerosError = uiState.CapacidadPasajeros <= 0
    val altitudMaximaError = uiState.AltitudMaxima <= 0
    val licenciaError = uiState.Licencia.isBlank()
    val isFormValid = !estadoIdError && !modeloAvionError && !descripcionCategoriaError &&
            !registracionError && !costoXHoraError && !descripcionAeronaveError &&
            !velocidadMaximaError && !descripcionMotorError && !capacidadCombustibleError &&
            !consumoXHoraError && !pesoError && !rangoError && !capacidadPasajerosError &&
            !altitudMaximaError && !licenciaError

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
                onEvent(AeronaveEvent.ResetSuccessMessage)
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
                        text = if (uiState.AeronaveId == null) "Nueva Aeronave" else "Editar Aeronave",
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
                .verticalScroll(rememberScrollState()) // Habilita el desplazamiento vertical
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
                    Text(if (uiState.AeronaveId == null) "Nueva Aeronave" else "Editar Aeronave")

                    OutlinedTextField(
                        value = uiState.AeronaveId?.toString() ?: "Nuevo",
                        onValueChange = {},
                        label = { Text("ID Aeronave") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.estadoId?.toString() ?: "",
                        onValueChange = { onEvent(AeronaveEvent.EstadoIdChange(it.toIntOrNull() ?: 0)) },
                        label = { Text("ID Estado") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = estadoIdError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (estadoIdError) {
                        Text(
                            text = "El ID de estado debe ser mayor que cero",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.ModeloAvion,
                        onValueChange = { onEvent(AeronaveEvent.ModeloAvionChange(it)) },
                        label = { Text("Modelo de Avión") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = modeloAvionError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (modeloAvionError) {
                        Text(
                            text = "El modelo de avión no puede estar vacío",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.DescripcionCategoria,
                        onValueChange = { onEvent(AeronaveEvent.DescripcionCategoriaChange(it)) },
                        label = { Text("Descripción de Categoría") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = descripcionCategoriaError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (descripcionCategoriaError) {
                        Text(
                            text = "La descripción de categoría no puede estar vacía",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.Registracion,
                        onValueChange = { onEvent(AeronaveEvent.RegistracionChange(it)) },
                        label = { Text("Registración") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = registracionError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (registracionError) {
                        Text(
                            text = "La registración no puede estar vacía",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.CostoXHora?.toString() ?: "",
                        onValueChange = { onEvent(AeronaveEvent.CostoXHoraChange(it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("Costo por Hora") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = costoXHoraError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (costoXHoraError) {
                        Text(
                            text = "El costo por hora debe ser mayor que cero",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.DescripcionAeronave,
                        onValueChange = { onEvent(AeronaveEvent.DescripcionAeronaveChange(it)) },
                        label = { Text("Descripción de Aeronave") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = descripcionAeronaveError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (descripcionAeronaveError) {
                        Text(
                            text = "La descripción de la aeronave no puede estar vacía",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.VelocidadMaxima?.toString() ?: "",
                        onValueChange = { onEvent(AeronaveEvent.VelocidadMaximaChange(it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("Velocidad Máxima") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = velocidadMaximaError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (velocidadMaximaError) {
                        Text(
                            text = "La velocidad máxima debe ser mayor que cero",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.DescripcionMotor,
                        onValueChange = { onEvent(AeronaveEvent.DescripcionMotorChange(it)) },
                        label = { Text("Descripción del Motor") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = descripcionMotorError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (descripcionMotorError) {
                        Text(
                            text = "La descripción del motor no puede estar vacía",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.CapacidadCombustible.toString(),
                        onValueChange = { onEvent(AeronaveEvent.CapacidadCombustibleChange(it.toIntOrNull() ?: 0)) },
                        label = { Text("Capacidad de Combustible") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = capacidadCombustibleError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (capacidadCombustibleError) {
                        Text(
                            text = "La capacidad de combustible debe ser mayor que cero",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.ConsumoXHora.toString(),
                        onValueChange = { onEvent(AeronaveEvent.ConsumoXHoraChange(it.toIntOrNull() ?: 0)) },
                        label = { Text("Consumo por Hora") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = consumoXHoraError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (consumoXHoraError) {
                        Text(
                            text = "El consumo por hora debe ser mayor que cero",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.Peso?.toString() ?: "",
                        onValueChange = { onEvent(AeronaveEvent.PesoChange(it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("Peso") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = pesoError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (pesoError) {
                        Text(
                            text = "El peso debe ser mayor que cero",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.Rango.toString(),
                        onValueChange = { onEvent(AeronaveEvent.RangoChange(it.toIntOrNull() ?: 0)) },
                        label = { Text("Rango") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = rangoError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (rangoError) {
                        Text(
                            text = "El rango debe ser mayor que cero",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.CapacidadPasajeros.toString(),
                        onValueChange = { onEvent(AeronaveEvent.CapacidadPasajerosChange(it.toIntOrNull() ?: 0)) },
                        label = { Text("Capacidad de Pasajeros") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = capacidadPasajerosError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (capacidadPasajerosError) {
                        Text(
                            text = "La capacidad de pasajeros debe ser mayor que cero",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.AltitudMaxima.toString(),
                        onValueChange = { onEvent(AeronaveEvent.AltitudMaximaChange(it.toIntOrNull() ?: 0)) },
                        label = { Text("Altitud Máxima") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = altitudMaximaError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (altitudMaximaError) {
                        Text(
                            text = "La altitud máxima debe ser mayor que cero",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.Licencia,
                        onValueChange = { onEvent(AeronaveEvent.LicenciaChange(it)) },
                        label = { Text("Licencia") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = licenciaError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (licenciaError) {
                        Text(
                            text = "La licencia no puede estar vacía",
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
                            onClick = { onEvent(AeronaveEvent.New) },
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
                                    if (uiState.AeronaveId == null) {
                                        onEvent(AeronaveEvent.postAeronave) // Crear nueva aeronave
                                    } else {
                                        onEvent(AeronaveEvent.Save) // Editar aeronave existente
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
                            Text(text = if (uiState.AeronaveId == null) "Guardar" else "Actualizar")
                        }
                    }
                }
            }
        }
    }
}