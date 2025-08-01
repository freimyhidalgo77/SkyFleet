package edu.ucne.skyplanerent.presentation.login

import androidx.compose.foundation.Image
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import edu.ucne.skyplanerent.R
import edu.ucne.skyplanerent.data.local.entity.UserRegisterAccount
import edu.ucne.skyplanerent.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date
import android.app.DatePickerDialog
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    userRepository: UserRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

// Fecha como objeto Date
    var selectedDate by remember { mutableStateOf<Date?>(null) }

// Mostrar string formateado para la UI
    val formattedDate = selectedDate?.let { dateFormat.format(it) } ?: "Seleccionar fecha"

    val context = LocalContext.current


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)
        .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top
    ){

    Image(
        painter = painterResource(id = R.drawable.logoskyfleet),
        contentDescription = "Logo de Skyfleet",
        modifier = Modifier
            .height(100.dp)
            .fillMaxSize()
    )


        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )


        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )


        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electronico") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )


        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )


        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = formatPhoneNumber(it)
            },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )


        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Direccion") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(25.dp))


        Button(
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        selectedDate = calendar.time
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF0F0F0),
                contentColor = Color.Black
            )
        ) {
            Text(text = formattedDate)
        }
        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                auth.createUserWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Guardar en Room
                            val user = UserRegisterAccount(
                                nombre = nombre,
                                apellido = apellido,
                                correo = correo,
                                telefono = telefono,
                                contrasena = contrasena,
                                direcccion = direccion,
                                fecha = selectedDate ?: Date()
                            )

                            coroutineScope.launch {
                                try {
                                    userRepository.insertUser(user)
                                    onRegisterSuccess()
                                } catch (e: Exception) {
                                    error = "Error al guardar datos localmente: ${e.message}"
                                }
                            }
                        } else {
                            error = task.exception?.message
                        }
                    }
            },
            enabled = correo.isNotBlank() && contrasena.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0A80ED),
                contentColor = Color.White
            )
        ) {
            Text("Registrar")
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red)
        }
    }
}

fun formatPhoneNumber(number: String): String {
    // Elimina todo lo que no sea dígito
    val digits = number.filter { it.isDigit() }

    // Máximo 10 dígitos
    val trimmed = digits.take(10)

    return when (trimmed.length) {
        in 1..3 -> trimmed
        in 4..6 -> "${trimmed.substring(0, 3)}-${trimmed.substring(3)}"
        in 7..10 -> "${trimmed.substring(0, 3)}-${trimmed.substring(3, 6)}-${trimmed.substring(6)}"
        else -> trimmed
    }
}

