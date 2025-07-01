package edu.ucne.skyplanerent.presentation.ruta

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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RutaScreen(
    rutaId: Int?,
    viewModel: RutaViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(rutaId) {
        println("id $rutaId")
        rutaId?.let {
            if (it > 0){
                viewModel.selectedRuta(it)
            }
        }
    }
    RutaBodyScreen(
        uiState = uiState,
        viewModel::onEvent,
        goBack = goBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaBodyScreen(
    uiState: RutaUiState,
    onEvent: (RutaEvent) -> Unit,
    goBack: () -> Unit
) {
    val aeronaveIdError = uiState.aeronaveId <= 0
    val origenError = uiState.origen.isNullOrBlank()
    val destinoError = uiState.destino.isNullOrBlank()
    val distanciaError = uiState.distancia.isNullOrBlank()
    val duracionEstimadaError = uiState.duracionEstimada.isNullOrBlank()
    val isFormValid = !origenError && !aeronaveIdError && !destinoError && !distanciaError && !duracionEstimadaError

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
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
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Registro de rutas")

                    OutlinedTextField(
                        value = uiState.rutaId?.toString() ?: "Nuevo",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = false
                    )

                    OutlinedTextField(
                        value = uiState.origen ?: "",
                        onValueChange = { onEvent(RutaEvent.OrigenChange(it)) },
                        label = { Text("Digite el origen") },
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

                    OutlinedTextField(
                        value = uiState.destino ?: "",
                        onValueChange = { onEvent(RutaEvent.DestinoChange(it)) },
                        label = { Text("Digite el destino") },
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

                    OutlinedTextField(
                        value = uiState.distancia ?: "",
                        onValueChange = { onEvent(RutaEvent.DistanciaChange(it)) },
                        label = { Text("Digite la distancia") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = distanciaError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (distanciaError) {
                        Text(
                            text = "La distancia no puede estar vacía",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }


                    OutlinedTextField(
                        value = uiState.duracionEstimada ?: "",
                        onValueChange = { onEvent(RutaEvent.DuracionEstimadaChange(it)) },
                        label = { Text("Digite la duracion Estimada") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = duracionEstimadaError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (duracionEstimadaError) {
                        Text(
                            text = "La duracion Estimada no puede estar vacía",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    OutlinedTextField(
                        value = uiState.aeronaveId.toString(),
                        onValueChange = { input ->
                            val aeronaveId = input.toIntOrNull() ?: 0
                            onEvent(RutaEvent.AeronaveChange(aeronaveId))
                        },
                        label = { Text("Aeronave de la ruta") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = aeronaveIdError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (aeronaveIdError) {
                        Text(
                            text = "La Aeronave debe ser mayor a 0",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.padding(2.dp))
                    uiState.errorMessage?.let {
                        Text(text = it, color = Color.Red)
                    }
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
                                    onEvent(RutaEvent.Save)
                                    goBack()
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
                            Text(text = "Guardar")
                        }
                    }
                }
            }
        }
    }
}