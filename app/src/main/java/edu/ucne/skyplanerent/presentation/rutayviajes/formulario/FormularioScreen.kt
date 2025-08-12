package edu.ucne.skyplanerent.presentation.rutayviajes.formulario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.reserva.ReservaViewModel
import kotlin.reflect.KFunction1


@Composable
fun FormularioScreen (
    formularioId:Int,
    viewModel: FormularioViewModel = hiltViewModel(),
    aeronaveViewModel: AeronaveViewModel = hiltViewModel(),
    aeronaveSeleccionadaId: Int?,
    goBack: (Int) -> Unit,
    goToPago: (Int) -> Unit,
    currentUserEmail: String?


    ) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val aeronaveUiState = aeronaveViewModel.uiState.collectAsStateWithLifecycle()

    // Obtén la aeronave usando el ID proporcionado
    val selectedAeronave by remember(aeronaveSeleccionadaId) {
        derivedStateOf {
            aeronaveSeleccionadaId?.let { id ->
                aeronaveUiState.value.aeronaves.find { it.aeronaveId == id }
            }
        }
    }

    // Pasa solo la capacidad máxima
    val capacidadMaxima = selectedAeronave?.capacidadPasajeros ?: 0

    FormularioBodyScreen(
        uiState = uiState.value,
       // selectedAeronave = selectedAeronave,
        onChangeNombre = viewModel::onNombreChange,
        onChangeApellido = viewModel::onApellidoChange,
        onChangeTelefono = viewModel::onTelefonoChange,
        onChangeCorreo = viewModel::onCorreoChange,
        onChangePasaporte = viewModel::onPasaporteChange,
        onChangeCiudad = viewModel::onCiudadResidenciaChange,
        onChangePasajero = viewModel::onChangePasajero,
        save = { viewModel.saveAndReturnId { id -> goToPago(id) } },
       // nuevo = viewModel::nuevoFormulario,
       // goBack = goBack,
        goToPago = goToPago,
        aeronaveUiState = aeronaveUiState.value,
        capacidadMaxima = capacidadMaxima,
        currentUserEmail = currentUserEmail

    )
}

@Composable
fun FormularioBodyScreen(
    uiState: FormularioUiState,
    aeronaveUiState: AeronaveUiState,
    //aeronaveViewModel: AeronaveViewModel = hiltViewModel(),
    reservaViewModel: ReservaViewModel = hiltViewModel(),
    viewModel: FormularioViewModel =  hiltViewModel(),
    onChangeNombre:(String)->Unit,
    onChangeApellido: KFunction1<String, Unit>,
    onChangeCorreo: KFunction1<String, Unit>,
    onChangeTelefono: KFunction1<String, Unit>,
    onChangePasaporte: KFunction1<String, Unit>,
    onChangeCiudad: KFunction1<String, Unit>,
    onChangePasajero: KFunction1<Int, Unit>,
    save:()->Unit,
    //goBack: (Int) -> Unit,
    goToPago:(Int)->Unit,
    //selectedAeronave: AeronaveDTO?,
    capacidadMaxima: Int,
    currentUserEmail: String?


) {

    val scrollState = rememberScrollState()

    val idAeronaveSeleccionada by reservaViewModel.tipoAeronaveSeleccionadaId.collectAsState()
    val aeronaveSeleccionada =
        aeronaveUiState.aeronaves.find { it.aeronaveId == idAeronaveSeleccionada }

    val capacidadMostrar = capacidadMaxima
    val showCapacityAlert = remember { mutableStateOf(false) }

    LaunchedEffect(currentUserEmail) {
        viewModel.loadUserData(currentUserEmail)
    }


    if (showCapacityAlert.value) {
        AlertDialog(
            onDismissRequest = { showCapacityAlert.value = false },
            title = { Text("Capacidad máxima excedida") },
            text = { Text("Esta aeronave solo soporta $capacidadMostrar pasajeros.") },
            confirmButton = {
                Button(onClick = { showCapacityAlert.value = false }) {
                    Text("Entendido")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top
    ) {


        Text(
            text = "Formulario de reserva",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,

            )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = onChangeNombre,
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)

        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.apellido,
            onValueChange = onChangeApellido,
            label = { Text("Apellido") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)

        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.correo,
            onValueChange = onChangeCorreo,
            label = { Text("Correo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)

        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.telefono,
            onValueChange = {
                val formatted = formatPhoneNumber(it)
                onChangeTelefono(formatted)
            },
            label = { Text("Teléfono") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.pasaporte,
            onValueChange = onChangePasaporte,
            label = { Text("Pasaporte") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.ciudadResidencia,
            onValueChange = onChangeCiudad,
            label = { Text("Ciudad de residencia") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)

        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Cantidad de pasajeros",
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón de decremento (restar 1)
                IconButton(
                    onClick = {
                        if (uiState.cantidadPasajeros > 1) {
                            onChangePasajero(uiState.cantidadPasajeros - 1)
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (uiState.cantidadPasajeros > 1) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Decrementar",
                        tint = if (uiState.cantidadPasajeros > 1) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Contador de pasajeros
                Text(
                    text = uiState.cantidadPasajeros.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.width(24.dp),
                    textAlign = TextAlign.Center
                )

                // Botón de incremento (sumar 1)
                IconButton(
                    onClick = {
                        if (uiState.cantidadPasajeros < capacidadMostrar) {
                            onChangePasajero(uiState.cantidadPasajeros + 1)
                        } else {
                            showCapacityAlert.value = true
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (uiState.cantidadPasajeros < capacidadMostrar)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.errorContainer,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Incrementar",
                        tint = if (uiState.cantidadPasajeros < capacidadMostrar)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Text(
            text = "Máximo: $capacidadMostrar pasajeros",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(30.dp))

        val puedeContinuar = uiState.nombre.isNotBlank()
                && uiState.apellido.isNotBlank()
                && uiState.correo.isNotBlank()
                && uiState.telefono.isNotBlank()
                && uiState.pasaporte.isNotBlank()
                && uiState.ciudadResidencia.isNotBlank()
                && uiState.cantidadPasajeros != 0
        Button(
            onClick = {
                save()
            },
            enabled = puedeContinuar,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (puedeContinuar) Color(0xFF0A80ED) else Color.LightGray,
                contentColor = Color.White
            )
        ) {
            Text("Siguiente")
        }

    }

}


fun formatPhoneNumber(input: String): String {
    val digits = input.filter { it.isDigit() }.take(10)
    return when (digits.length) {
        in 1..3 -> digits
        in 4..6 -> "${digits.substring(0, 3)}-${digits.substring(3)}"
        in 7..10 -> "${digits.substring(0, 3)}-${digits.substring(3, 6)}-${digits.substring(6)}"
        else -> digits
    }
}

