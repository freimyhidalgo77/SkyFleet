package edu.ucne.skyplanerent.presentation.aeronave

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.skyplanerent.presentation.categoriaaeronave.CategoriaAeronaveViewModel
import kotlinx.coroutines.launch

@Composable
fun AeronaveScreen(
    aeronaveId: Int? = null,
    viewModel: AeronaveViewModel = hiltViewModel(),
    categoriaViewModel: CategoriaAeronaveViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Lanzador para seleccionar imágenes
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { viewModel.onEvent(AeronaveEvent.ImageSelected(it)) }
    }

    // Lanzador para permisos
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) { // Cambiado de aeronaveId a Unit
        if (uiState.aeronaveId == null && uiState.modeloAvion.isBlank() && uiState.registracion.isBlank()) {
            if (aeronaveId != null && aeronaveId > 0) {
                viewModel.onEvent(AeronaveEvent.GetAeronave(aeronaveId))
            } else {
                viewModel.onEvent(AeronaveEvent.New)
            }
        }
    }

    AeronaveBodyScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        goBack = goBack,
        categoriaViewModel = categoriaViewModel,
        pickImage = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AeronaveBodyScreen(
    uiState: AeronaveUiState,
    onEvent: (AeronaveEvent) -> Unit,
    goBack: () -> Unit,
    categoriaViewModel: CategoriaAeronaveViewModel,
    pickImage: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val showDialog = remember { mutableStateOf(false) }
    val categoriaUiState by categoriaViewModel.uiState.collectAsState()
    val expanded = remember { mutableStateOf(false) }

    // Validaciones
    val estadoIdError = uiState.estadoId == null || uiState.estadoId <= 0
    val modeloAvionError = uiState.modeloAvion.isBlank()
    val descripcionCategoriaError = uiState.descripcionCategoria.isBlank()
    val registracionError = uiState.registracion.isBlank()
    val costoXHoraError = uiState.costoXHora == null || uiState.costoXHora <= 0.0
    val descripcionAeronaveError = uiState.descripcionAeronave.isBlank()
    val velocidadMaximaError = uiState.velocidadMaxima == null || uiState.velocidadMaxima <= 0.0
    val descripcionMotorError = uiState.descripcionMotor.isBlank()
    val capacidadCombustibleError = uiState.capacidadCombustible <= 0
    val consumoXHoraError = uiState.consumoXHora <= 0
    val pesoError = uiState.peso == null || uiState.peso <= 0.0
    val rangoError = uiState.rango <= 0
    val capacidadPasajerosError = uiState.capacidadPasajeros <= 0
    val altitudMaximaError = uiState.altitudMaxima <= 0
    val licenciaError = uiState.licencia.isBlank()
    val isFormValid = !estadoIdError && !modeloAvionError && !descripcionCategoriaError &&
            !registracionError && !costoXHoraError && !descripcionAeronaveError &&
            !velocidadMaximaError && !descripcionMotorError && !capacidadCombustibleError &&
            !consumoXHoraError && !pesoError && !rangoError && !capacidadPasajerosError &&
            !altitudMaximaError && !licenciaError

    // Obtener la descripción de la categoría seleccionada
    val selectedCategoria = categoriaUiState.categorias.find { it.categoriaId == uiState.estadoId }?.descripcionCategoria ?: "Seleccione una categoría"

    // Diálogo de éxito
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                onEvent(AeronaveEvent.New) // Reiniciar el estado del formulario
                onEvent(AeronaveEvent.ResetSuccessMessage) // Reiniciar estado de éxito
            },
            title = {
                Text(
                    text = "Aeronave Guardada Correctamente",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AirplanemodeActive,
                        contentDescription = "Aeronave Guardada Correctamente",
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
                        onEvent(AeronaveEvent.New) // Reiniciar el estado del formulario
                        onEvent(AeronaveEvent.ResetSuccessMessage) // Reiniciar estado de éxito
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

    // Mostrar diálogo para éxito o Snackbar para error
    LaunchedEffect(uiState.isSuccess, uiState.errorMessage) {
        if (uiState.isSuccess && !uiState.successMessage.isNullOrBlank()) {
            showDialog.value = true
            onEvent(AeronaveEvent.ResetSuccessMessage)
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
                        text = if (uiState.aeronaveId == null) "Nueva Aeronave" else "Editar Aeronave",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                .verticalScroll(rememberScrollState())
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
                    Text(if (uiState.aeronaveId == null) "Nueva Aeronave" else "Editar Aeronave")

                    // Mostrar la imagen guardada o la seleccionada
                    when {
                        uiState.imageUrl != null -> {
                            AsyncImage(
                                model = uiState.imageUrl,
                                contentDescription = "Imagen de la aeronave",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        uiState.imageUri != null -> {
                            AsyncImage(
                                model = uiState.imageUri,
                                contentDescription = "Imagen de la aeronave",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Spacer(modifier = Modifier.height(200.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo para URL de la imagen (solo al editar)
                    if (uiState.aeronaveId != null) {
                        OutlinedTextField(
                            value = uiState.imageUrl ?: "",
                            onValueChange = { onEvent(AeronaveEvent.ImageUrlChange(it)) },
                            label = { Text("URL de la Imagen") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Blue,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Blue,
                                errorBorderColor = Color.Red
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Botón para seleccionar imagen (solo para nueva aeronave)
                    if (uiState.aeronaveId == null) {
                        OutlinedButton(
                            onClick = pickImage,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Blue),
                            border = BorderStroke(1.dp, Color.Blue)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = "Seleccionar imagen")
                            Text("Seleccionar Imagen")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // DropdownMenu para categorías
                    ExposedDropdownMenuBox(
                        expanded = expanded.value,
                        onExpandedChange = { expanded.value = !expanded.value },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCategoria,
                            onValueChange = {},
                            label = { Text("Categoría") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            isError = estadoIdError,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Blue,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Blue,
                                errorBorderColor = Color.Red
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false }
                        ) {
                            categoriaUiState.categorias.forEach { categoria ->
                                DropdownMenuItem(
                                    text = { Text(categoria.descripcionCategoria) },
                                    onClick = {
                                        onEvent(AeronaveEvent.EstadoIdChange(categoria.categoriaId))
                                        expanded.value = false
                                    }
                                )
                            }
                        }
                    }
                    if (estadoIdError) {
                        Text(
                            text = "Debe seleccionar una categoría",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.modeloAvion,
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
                        value = uiState.descripcionCategoria,
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
                        value = uiState.registracion,
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
                        value = uiState.costoXHora?.toString() ?: "",
                        onValueChange = {
                            if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                onEvent(AeronaveEvent.CostoXHoraChange(it.toDoubleOrNull() ?: 0.0))
                            }
                        },
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
                        value = uiState.descripcionAeronave,
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
                        value = uiState.velocidadMaxima?.toString() ?: "",
                        onValueChange = {
                            if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                onEvent(AeronaveEvent.VelocidadMaximaChange(it.toDoubleOrNull() ?: 0.0))
                            }
                        },
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
                        value = uiState.descripcionMotor,
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
                        value = uiState.capacidadCombustible.toString(),
                        onValueChange = {
                            if (it.isEmpty() || it.toIntOrNull() != null) {
                                onEvent(AeronaveEvent.CapacidadCombustibleChange(it.toIntOrNull() ?: 0))
                            }
                        },
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
                        value = uiState.consumoXHora.toString(),
                        onValueChange = {
                            if (it.isEmpty() || it.toIntOrNull() != null) {
                                onEvent(AeronaveEvent.ConsumoXHoraChange(it.toIntOrNull() ?: 0))
                            }
                        },
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
                        value = uiState.peso?.toString() ?: "",
                        onValueChange = {
                            if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                onEvent(AeronaveEvent.PesoChange(it.toDoubleOrNull() ?: 0.0))
                            }
                        },
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
                        value = uiState.rango.toString(),
                        onValueChange = {
                            if (it.isEmpty() || it.toIntOrNull() != null) {
                                onEvent(AeronaveEvent.RangoChange(it.toIntOrNull() ?: 0))
                            }
                        },
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
                        value = uiState.capacidadPasajeros.toString(),
                        onValueChange = {
                            if (it.isEmpty() || it.toIntOrNull() != null) {
                                onEvent(AeronaveEvent.CapacidadPasajerosChange(it.toIntOrNull() ?: 0))
                            }
                        },
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
                        value = uiState.altitudMaxima.toString(),
                        onValueChange = {
                            if (it.isEmpty() || it.toIntOrNull() != null) {
                                onEvent(AeronaveEvent.AltitudMaximaChange(it.toIntOrNull() ?: 0))
                            }
                        },
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
                        value = uiState.licencia,
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
                                    if (uiState.aeronaveId == null) {
                                        onEvent(AeronaveEvent.PostAeronave)
                                    } else {
                                        onEvent(AeronaveEvent.Save)
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
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "save button"
                            )
                            Text(text = if (uiState.aeronaveId == null) "Guardar" else "Actualizar")
                        }
                    }
                }
            }
        }
    }
}