package edu.ucne.skyplanerent.presentation.rutayviajes.ruta


import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.presentation.UiEvent
import kotlinx.coroutines.launch

@Composable
fun RutaScreen(
    rutaId: Int? = null,
    viewModel: RutaViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initialize(rutaId)
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            // Restablecer el mensaje después de mostrar el Toast
            viewModel.onEvent(RutaEvent.ResetSuccessMessage)
            Log.d("RutaScreen", "Toast mostrado: $message")
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
                        text = uiState.title,
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
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.title,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    RutaTextField(
                        value = uiState.origen.orEmpty(),
                        onValueChange = { onEvent(RutaEvent.OrigenChange(it)) },
                        label = "Origen",
                        isError = uiState.errorOrigen.isNotBlank(),
                        errorMessage = uiState.errorOrigen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    RutaTextField(
                        value = uiState.destino.orEmpty(),
                        onValueChange = { onEvent(RutaEvent.DestinoChange(it)) },
                        label = "Destino",
                        isError = uiState.errorDestino.isNotBlank(),
                        errorMessage = uiState.errorDestino
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    RutaTextField(
                        value = if (uiState.distancia == 0.0) "" else uiState.distancia.toString(),
                        onValueChange = {
                            onEvent(RutaEvent.DistanciaChange(it.toDoubleOrNull() ?: 0.0))
                        },
                        label = "Distancia (km)",
                        isError = uiState.errorDistancia.isNotBlank(),
                        errorMessage = uiState.errorDistancia,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { /* Focus next field */ })
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    RutaTextField(
                        value = if (uiState.duracionEstimada == 0) "" else uiState.duracionEstimada.toString(),
                        onValueChange = {
                            onEvent(RutaEvent.DuracionEstimadaChange(it.toIntOrNull() ?: 0))
                        },
                        label = "Duración Estimada (min)",
                        isError = uiState.errorDuracion.isNotBlank(),
                        errorMessage = uiState.errorDuracion,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { /* Submit or hide keyboard */ })
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ErrorMessageText(errorMessage = uiState.errorMessage.orEmpty())

                    Spacer(modifier = Modifier.height(16.dp))

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
                                contentDescription = "Nuevo",
                                tint = Color.Blue
                            )
                            Text("Nuevo")
                        }
                        OutlinedButton(
                            onClick = { onEvent(RutaEvent.SubmitRuta) },
                            enabled = uiState.isFormValid,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = uiState.submitButtonContentColor,
                                disabledContentColor = uiState.submitButtonDisabledContentColor
                            ),
                            border = BorderStroke(1.dp, uiState.submitButtonBorderColor),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(text = uiState.submitButtonText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RutaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorMessage: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Blue,
                errorBorderColor = Color.Red,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        ErrorMessageText(
            errorMessage = errorMessage,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}

@Composable
fun ErrorMessageText(
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = errorMessage,
        color = Color.Red,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
            .alpha(if (errorMessage.isNotBlank()) 1f else 0f)
            .height(if (errorMessage.isNotBlank()) 16.dp else 0.dp)
    )
}