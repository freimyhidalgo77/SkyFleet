package edu.ucne.skyplanerent.presentation.tipovuelo

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
import edu.ucne.skyplanerent.presentation.ruta.RutaViewModel

@Composable
fun TipoVueloScreen(
    vueloId: Int?,
    viewModel: TipoVueloViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(vueloId) {
        println("id $vueloId")
        vueloId?.let {
            if (it > 0){
                viewModel.selectedTipoVuelo(it)
            }
        }
    }
    TipoVueloBodyScreen(
        uiState = uiState,
        viewModel::onEvent,
        goBack = goBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoVueloBodyScreen(
    uiState: TipoVueloUiState,
    onEvent: (TipoVueloEvent) -> Unit,
    goBack: () -> Unit
) {
    val rutaIdError = uiState.rutaId <= 0
    val tipoClienteError = uiState.tipoclienteId <= 0
    val descripcionTipoVueloError = uiState.descripcionTipoVuelo.isNullOrBlank()
    val precioError = uiState.precio <= 0.0
    val isFormValid = !rutaIdError && !tipoClienteError && !descripcionTipoVueloError && !precioError

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
                    Text("Registro de tipo de Vuelo")

                    OutlinedTextField(
                        value = uiState.vueloId?.toString() ?: "Nuevo",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = false
                    )

                    OutlinedTextField(
                        value = uiState.rutaId.toString(),
                        onValueChange = { input ->
                            val rutaId = input.toIntOrNull() ?: 0
                            onEvent(TipoVueloEvent.RutaChange(rutaId))
                        },
                        label = { Text("ID de la ruta") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = rutaIdError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (rutaIdError) {
                        Text(
                            text = "El ID de la ruta debe ser mayor a 0",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    OutlinedTextField(
                        value = uiState.tipoclienteId.toString(),
                        onValueChange = { input ->
                            val tipoClienteId = input.toIntOrNull() ?: 0
                            onEvent(TipoVueloEvent.TipoClienteChange(tipoClienteId))
                        },
                        label = { Text("ID del tipo de cliente") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = tipoClienteError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (tipoClienteError) {
                        Text(
                            text = "El ID del tipo de cliente debe ser mayor a 0",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    OutlinedTextField(
                        value = uiState.descripcionTipoVuelo ?: "",
                        onValueChange = { onEvent(TipoVueloEvent.DescripcionTipoVueloChange(it)) },
                        label = { Text("Descripción del tipo de vuelo") },
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
                            text = "La descripción del tipo de vuelo no puede estar vacía",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    OutlinedTextField(
                        value = uiState.precio.toString(),
                        onValueChange = { input ->
                            val precio = input.toDoubleOrNull() ?: 0.0
                            onEvent(TipoVueloEvent.PrecioChange(precio))
                        },
                        label = { Text("Precio") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = precioError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Blue,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (precioError) {
                        Text(
                            text = "El precio debe ser mayor a 0",
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
                                    onEvent(TipoVueloEvent.Save)
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