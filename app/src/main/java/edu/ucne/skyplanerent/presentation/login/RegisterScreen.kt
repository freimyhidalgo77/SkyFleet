package edu.ucne.skyplanerent.presentation.login

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ucne.skyplanerent.R
import edu.ucne.skyplanerent.data.local.entity.UserRegisterAccount
import edu.ucne.skyplanerent.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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

    //val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    var selectedDate by remember { mutableStateOf<Date?>(null) }

    val formattedDate = selectedDate?.let { dateFormat.format(it) } ?: "Seleccionar fecha"

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    var confirmarContrasena by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var passwordsMatchError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }


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
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                val image = if (passwordVisible)
                    painterResource(id = R.drawable.eyeclosed)
                else
                    painterResource(id = R.drawable.eyeopen)

                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = image,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Gray
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = {
                confirmarContrasena = it
                // Validar coincidencia cuando se cambia
                passwordsMatchError = if (it != contrasena) "Las contraseñas no coinciden" else null
            },
            label = { Text("Confirmar Contraseña") },
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                val image = if (confirmPasswordVisible)
                    painterResource(id = R.drawable.eyeclosed)
                else
                    painterResource(id = R.drawable.eyeopen)

                IconButton(
                    onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                    modifier = Modifier.size(24.dp))
                {
                    Icon(
                        painter = image,
                        contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Gray
                    )
                }

            }
        )

        passwordsMatchError?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }


        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = formatPhoneNumber(it)
            },
            label = { Text("Teléfono") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                showSpinnerDatePicker(
                    context = context,
                    initialDate = selectedDate ?: Calendar.getInstance().time,
                    onDateSelected = { date -> selectedDate = date }
                )
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

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                    errorMessage = "Por favor ingresa un correo electrónico válido"
                    return@Button
                }

                if (contrasena.length < 6) {
                    errorMessage = "La contraseña debe tener al menos 6 caracteres"
                    return@Button
                }

                auth.createUserWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Crear objeto de usuario
                            val user = UserRegisterAccount(
                                nombre = nombre,
                                apellido = apellido,
                                correo = correo,
                                telefono = telefono,
                                contrasena = contrasena,
                                direcccion = direccion,
                                fecha = selectedDate ?: Date()
                            )

                            // Guardar en Room (local)
                            coroutineScope.launch {
                                try {
                                    userRepository.insertUser(user)

                                    // Guardar en Firestore (nube)
                                    val db = Firebase.firestore
                                    val userData = hashMapOf(
                                        "nombre" to nombre,
                                        "apellido" to apellido,
                                        "correo" to correo,
                                        "telefono" to telefono,
                                        "direccion" to direccion,
                                        "fechaNacimiento" to selectedDate?.time // Guardamos el timestamp
                                    )

                                    db.collection("users")
                                        .document(auth.currentUser?.uid ?: "")
                                        .set(userData)
                                        .addOnSuccessListener {
                                            onRegisterSuccess()
                                        }
                                        .addOnFailureListener { e ->
                                            error = "Error al guardar en la nube: ${e.message}"
                                        }

                                } catch (e: Exception) {
                                    error = "Error al guardar datos localmente: ${e.message}"
                                }
                            }
                        } else {
                            error = task.exception?.message
                        }
                    }
            },
            enabled = correo.isNotBlank() && contrasena.isNotBlank() && nombre.isNotBlank() && apellido.isNotBlank()
                    && telefono.isNotBlank() && direccion.isNotBlank() && formattedDate != null,
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


private fun showSpinnerDatePicker(
    context: Context,
    initialDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance().apply { time = initialDate }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    // Crear el DatePickerDialog con estilo spinner
    val datePickerDialog = DatePickerDialog(
        context,
        android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
        { _, selectedYear, selectedMonth, selectedDay ->
            val newDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }.time
            onDateSelected(newDate)
        },
        year,
        month,
        day
    )


    try {
        // Para Android 5.0+ (API 21+)
        val datePickerField = datePickerDialog.javaClass.getDeclaredField("mDatePicker")
        datePickerField.isAccessible = true
        val datePicker = datePickerField.get(datePickerDialog)

        val method = datePicker.javaClass.getMethod("setCalendarViewShown", Boolean::class.java)
        method.invoke(datePicker, false)
    } catch (e: Exception) {
        // Si falla, intentamos otro enfoque
        try {
            datePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            // Enfoque alternativo para versiones más recientes
            val datePicker = datePickerDialog.findViewById<DatePicker>(android.R.id.button1)
            datePicker?.calendarViewShown = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    datePickerDialog.show()
}

fun formatPhoneNumber(number: String): String {

    val digits = number.filter { it.isDigit() }

    val trimmed = digits.take(10)

    return when (trimmed.length) {
        in 1..3 -> trimmed
        in 4..6 -> "${trimmed.substring(0, 3)}-${trimmed.substring(3)}"
        in 7..10 -> "${trimmed.substring(0, 3)}-${trimmed.substring(3, 6)}-${trimmed.substring(6)}"
        else -> trimmed
    }
}

