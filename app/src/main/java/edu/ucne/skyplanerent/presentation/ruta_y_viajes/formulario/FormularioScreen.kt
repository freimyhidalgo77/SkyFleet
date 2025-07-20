package edu.ucne.skyplanerent.presentation.ruta_y_viajes.formulario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.reflect.KFunction1


@Composable
fun FormularioScreen (
    formularioId:Int,
    viewModel: FormularioViewModel = hiltViewModel(),
    goBack: () -> Unit,
    goToPago: (Int) -> Unit

) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    FormularioBodyScreen(
        uiState = uiState.value,
        onChangeNombre = viewModel::onNombreChange,
        onChangeApellido = viewModel::onApellidoChange,
        onChangeTelefono = viewModel::onTelefonoChange,
        onChangeCorreo = viewModel::onCorreoChange,
        onChangePasaporte = viewModel::onPasaporteChange,
        onChangeCiudad = viewModel::onCiudadResidenciaChange,
        onChangePasajero = viewModel::onChangePasajero,
        save = viewModel::saveFormulario,
        nuevo = viewModel::nuevoFormulario,
        goBack = goBack,
        goToPago = goToPago

    )
}

@Composable
fun FormularioBodyScreen(
    uiState: FormularioUiState,
    onChangeNombre:(String)->Unit,
    onChangeApellido: KFunction1<String, Unit>,
    onChangeCorreo: KFunction1<String, Unit>,
    onChangeTelefono: KFunction1<String, Unit>,
    onChangePasaporte: KFunction1<String, Unit>,
    onChangeCiudad: KFunction1<String, Unit>,
    onChangePasajero: KFunction1<Int, Unit>,
    save:()->Unit,
    nuevo:()->Unit,
    goBack: () -> Unit,
    goToPago:(Int)->Unit

){

    val scrollState = rememberScrollState()

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
            onValueChange = onChangeTelefono,
            label = { Text("Telefono") },
            singleLine = true,
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


        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Cantidad pasageros") },
            value = uiState.cantidadPasajeros.toString(),
            shape = RoundedCornerShape(16.dp),
            onValueChange = {
                val pasajero = it.toIntOrNull() ?: 0
                onChangePasajero(pasajero)
            }
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
                goToPago(0)
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
