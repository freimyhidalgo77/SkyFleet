package edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.presentation.UiEvent
import kotlinx.coroutines.launch

@Composable
fun TipoVueloDetailsScreen(
    tipoVueloId: Int?,
    viewModel: TipoVueloViewModel = hiltViewModel(),
    goBack: () -> Unit,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(tipoVueloId) {
        tipoVueloId?.let {
            if (it > 0) {
                viewModel.onEvent(TipoVueloEvent.GetTipoVuelo(it))
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro que quieres eliminar el tipo de vuelo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.onEvent(TipoVueloEvent.Delete)
                        onDelete(tipoVueloId ?: 0)
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    TipoVueloDetailsBodyScreen(
        uiState = uiState,
        goBack = goBack,
        onDelete = { showDeleteDialog = true },
        onEdit = { onEdit(tipoVueloId ?: 0) },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoVueloDetailsBodyScreen(
    uiState: TipoVueloUiState,
    goBack: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Detalles del Tipo de Vuelo",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
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
                    } else if (uiState.tipoVueloId != null) {
                        Text(
                            text = "Tipo",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Blue
                        )
                        OutlinedTextField(
                            value = uiState.nombreVuelo,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                        )
                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Blue
                        )
                        OutlinedTextField(
                            value = uiState.descripcionTipoVuelo,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = onEdit,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Modificar Tipo Vuelo", color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onDelete,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Eliminar Tipo Vuelo", color = Color.White)
                            }
                        }
                    } else {
                        Text(
                            text = "No se encontraron detalles del tipo de vuelo",
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

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}