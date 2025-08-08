package edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Flight
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.presentation.UiEvent
import kotlinx.coroutines.launch
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoVueloScreen(
    tipoVueloId: Int? = null,
    viewModel: TipoVueloViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (uiState.tipoVueloId == null && uiState.nombreVuelo.isEmpty() && uiState.descripcionTipoVuelo.isEmpty()) {
            if (tipoVueloId != null && tipoVueloId > 0) {
                viewModel.onEvent(TipoVueloEvent.GetTipoVuelo(tipoVueloId))
            } else {
                viewModel.onEvent(TipoVueloEvent.New)
            }
        }
    }

    TipoVueloBodyScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        goBack = goBack,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoVueloBodyScreen(
    uiState: TipoVueloUiState,
    onEvent: (TipoVueloEvent) -> Unit,
    goBack: () -> Unit,
    viewModel: TipoVueloViewModel
) {
    val scope = rememberCoroutineScope()
    val showDialog = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Validaciones
    val nombreVueloError = uiState.nombreVuelo.isBlank()
    val descripcionTipoVueloError = uiState.descripcionTipoVuelo.isBlank()
    val isFormValid = !nombreVueloError && !descripcionTipoVueloError

    // Diálogo de éxito
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                onEvent(TipoVueloEvent.New) // Reiniciar el estado después de cerrar el diálogo
            },
            title = {
                Text(
                    text = "Tipo de Vuelo Guardado Correctamente",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Flight,
                        contentDescription = "Tipo de Vuelo Guardado Correctamente",
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
                        onEvent(TipoVueloEvent.New) // Reiniciar el estado después de cerrar el diálogo
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
            onEvent(TipoVueloEvent.ResetSuccessMessage)
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
                        text = if (uiState.tipoVueloId == null) "Nuevo Tipo de Vuelo" else "Editar Tipo de Vuelo",
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
) {
    innerPadding ->
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
                Text(if (uiState.tipoVueloId == null) "Nuevo Tipo de Vuelo" else "Editar Tipo de Vuelo")

                OutlinedTextField(
                    value = uiState.nombreVuelo,
                    onValueChange = { onEvent(TipoVueloEvent.NombreVueloChange(it)) },
                    label = { Text("Nombre del Vuelo") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nombreVueloError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.Blue,
                        errorBorderColor = Color.Red
                    )
                )
                if (nombreVueloError) {
                    Text(
                        text = "El nombre del vuelo no puede estar vacío",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.descripcionTipoVuelo,
                    onValueChange = { onEvent(TipoVueloEvent.DescripcionTipoVueloChange(it)) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = descripcionTipoVueloError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.Blue,
                        errorBorderColor = Color.Red
                    )
                )
                if (descripcionTipoVueloError) {
                    Text(
                        text = "La descripción no puede estar vacía",
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
                        onClick = { onEvent(TipoVueloEvent.New) },
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
                                if (uiState.tipoVueloId == null) {
                                    onEvent(TipoVueloEvent.postTipoVuelo)
                                } else {
                                    onEvent(TipoVueloEvent.Save)
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
                        Text(text = if (uiState.tipoVueloId == null) "Guardar" else "Actualizar")
                    }
                }
            }
        }
    }
 }
}