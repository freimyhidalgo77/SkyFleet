package edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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

@Composable
fun TipoVueloScreen(
    tipoVueloId: Int? = null,
    viewModel: TipoVueloViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(tipoVueloId) {
        tipoVueloId?.let {
            if (it > 0) {
                viewModel.onEvent(TipoVueloEvent.GetTipoVuelo(it))
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
    val snackbarHostState = remember { SnackbarHostState() }
    // Validaciones
    val nombreVueloError = uiState.nombreVuelo.isBlank()
    val descripcionTipoVueloError = uiState.descripcionTipoVuelo.isBlank()
    val isFormValid = !nombreVueloError && !descripcionTipoVueloError

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateUp -> goBack()
                is UiEvent.ShowSnackbar -> TODO()
            }
        }
    }

    // Mostrar Snackbar y navegar al éxito
    LaunchedEffect(uiState.isSuccess || !uiState.errorMessage.isNullOrBlank()) {
        if (uiState.isSuccess && !uiState.successMessage.isNullOrBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = uiState.successMessage,
                    duration = SnackbarDuration.Short
                )
                onEvent(TipoVueloEvent.ResetSuccessMessage)
                goBack() // Navegar a la lista después de mostrar el Snackbar
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = goBack,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "volver")
                }
            }
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
                    Text("Registro de Tipos de Vuelo")

                    OutlinedTextField(
                        value = uiState.tipoVueloId?.toString() ?: "Nuevo",
                        onValueChange = {},
                        label = { Text("ID Tipo de Vuelo") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.nombreVuelo,
                        onValueChange = {
                            onEvent(TipoVueloEvent.NombreVueloChange(it))
                            onEvent(TipoVueloEvent.LimpiarErrorMessageTipoClienteChange)
                        },
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
                        onValueChange = {
                            onEvent(TipoVueloEvent.DescripcionTipoVueloChange(it))
                            onEvent(TipoVueloEvent.LimpiarErrorMessageDescripcionTipoVueloChange)
                        },
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
                                    onEvent(TipoVueloEvent.postTipoVuelo)
                                    // goBack() removido de aquí, ahora se maneja en LaunchedEffect
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
                            Text(text = "Guardar")
                        }
                    }
                }
            }
        }
    }
}