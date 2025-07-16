package edu.ucne.skyplanerent.presentation.ruta_y_viajes.formulario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
    save:()->Unit,
    nuevo:()->Unit,
    goBack: () -> Unit,
    goToPago:(Int)->Unit

){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {


     Text(
         text = "Informacion personal",
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


        Button(
            onClick = { save()
                      goToPago(0)
                      },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0A80ED),
                contentColor = Color.White
            )

        ) {
            Text("Siguiente")
        }

        Spacer(modifier = Modifier.weight(1f))

        uiState.errorMessage?.let { menssage ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                content = {
                    Text(
                        text = menssage,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Red
                    )
                }
            )
        }
    }
}
