package edu.ucne.skyplanerent.presentation.reserva

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.rutayviajes.formulario.FormularioUiState
import edu.ucne.skyplanerent.presentation.rutayviajes.formulario.FormularioViewModel
import edu.ucne.skyplanerent.presentation.rutayviajes.formulario.formatPhoneNumber
import edu.ucne.skyplanerent.presentation.rutayviajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.rutayviajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo.TipoVueloUiState
import edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo.TipoVueloViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun formatDateToDMY(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "No disponible"

    return try {
        // Primero intentamos parsear el formato con hora
        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val date = inputFormat.parse(dateString)

        // Formateamos a día/mes/año (sin ceros a la izquierda)
        val outputFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        outputFormat.format(date!!)
    } catch (e: Exception) {
        try {
            // Si falla, intentamos con formato YYYY-MM-DD
            val parts = dateString.split("-")
            if (parts.size == 3) {
                "${parts[2].toInt()}/${parts[1].toInt()}/${parts[0]}"
            } else {
                dateString
            }
        } catch (e2: Exception) {
            dateString
        }
    }
}

@Composable
fun ReservaEditScreen(
    reservaId: Int,
    viewModel: ReservaViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    aeronaveViewModel: AeronaveViewModel = hiltViewModel(),
    formularioViewModel: FormularioViewModel = hiltViewModel(),
    goBack: (Int) -> Unit,
    aeronaveSeleccionadaId: Int?,
) {

    LaunchedEffect(reservaId) {
        viewModel.selectReserva(reservaId)
    }

    val aeronaveUiState = aeronaveViewModel.uiState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()
    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val formularioUiState = formularioViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.reservaSeleccionada?.formularioId) {
        uiState.reservaSeleccionada?.formularioId?.let { formularioId ->
            formularioViewModel.selectedFormulario(formularioId)
        }
    }

    LaunchedEffect(uiState.rutaId, uiState.categoriaId, uiState.pasajeros) {
        viewModel.actualizarPrecio()
    }


    if (uiState.reservaSeleccionada == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val aeronaveReserva = uiState.reservaSeleccionada?.let { reserva ->
        aeronaveUiState.value.aeronaves.find { it.aeronaveId == reserva.categoriaId }
    }

    val capacidadMaxima = aeronaveReserva?.capacidadPasajeros ?: 0


    ReservaEditBodyScreen(
        uiState = uiState,
        onChangePasajeros = viewModel::onChangePasajeros,
        save = {
            formularioViewModel.upedateFormulario()
            viewModel.updateReserva()
        },
        goBack = goBack,
        tipoVueloUiState = tipoVueloUiState,
        rutaUiState = rutaUiState,
        aeronaveUiState = aeronaveUiState.value,
        onChangeRuta = viewModel::onChangeRuta,
        onChangeAeronave = viewModel::categoriaIdChange,
        onChangeTipoVuelo = viewModel::onChangeTipoVuelo,
        onDateSelected = viewModel::onFechaChange,
        reservaId = reservaId,
        formularioViewModel = formularioViewModel,
        onChangeNombre = formularioViewModel::onNombreChange,
        formularioUiState = formularioUiState.value,
        capacidadMaxima = capacidadMaxima


    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaEditBodyScreen(
    uiState: UiState,
    formularioUiState: FormularioUiState,
    reservaId: Int,
    save: () -> Unit,
    goBack: (Int) -> Unit,
    tipoVueloUiState: TipoVueloUiState,
    rutaUiState: RutaUiState,
    aeronaveUiState: AeronaveUiState,
    onChangeRuta: (Int)-> Unit,
    onChangeAeronave:(Int)-> Unit,
    onChangeTipoVuelo: (Int) -> Unit,
    onDateSelected:(String)->Unit,
    onChangePasajeros: (Int) -> Unit,
    onChangeNombre:(String)->Unit,
    viewModel: ReservaViewModel = hiltViewModel(),
    formularioViewModel:FormularioViewModel = hiltViewModel(),
    capacidadMaxima: Int,
) {


    val reserva = uiState.reservaSeleccionada ?: return

    val formularioState by formularioViewModel.uiState.collectAsStateWithLifecycle()
    val tipoVuelo = tipoVueloUiState.tipovuelo.find { it.tipoVueloId == reserva.tipoVueloId }
    val ruta = rutaUiState.rutas.find { it.rutaId == reserva.rutaId }
    val aeronave = aeronaveUiState.aeronaves.find { it.aeronaveId == reserva.categoriaId }

    val fecha = uiState.fecha
    val tipoCliente by viewModel.tipoCliente.collectAsState()
    val licencia = uiState.licenciaPiloto

    var showRutaDialog by rememberSaveable { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val showCapacityAlert = remember { mutableStateOf(false) }


    if (showCapacityAlert.value) {
        AlertDialog(
            onDismissRequest = { showCapacityAlert.value = false },
            title = { Text("Capacidad máxima excedida") },
            text = { Text("Esta aeronave solo soporta $capacidadMaxima pasajeros.") },
            confirmButton = {
                Button(onClick = { showCapacityAlert.value = false }) {
                    Text("Entendido")
                }
            }
        )
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Modificar reserva",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { goBack(reservaId) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },

        bottomBar = {
            Button(
                onClick = { showConfirmationDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00F5A0),
                    contentColor = Color.Black
                )
            ) {

                Text("Confirmar cambios", color = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {


            if (showRutaDialog) {
                AlertDialog(
                    onDismissRequest = { showRutaDialog = false },
                    confirmButton = {},
                    title = { Text("Seleccionar Ruta") },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            rutaUiState.rutas.forEach { rutaItem ->
                                TextButton(
                                    onClick = {
                                        onChangeRuta(rutaItem.rutaId ?: 0)
                                        showRutaDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("${rutaItem.origen} → ${rutaItem.destino}")
                                }
                            }
                        }
                    },
                    containerColor = Color.White
                )

            }

            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialog = false },
                    title = { Text("Confirmar cambios") },
                    text = { Text("¿Estás seguro de que deseas guardar los cambios realizados?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showConfirmationDialog = false
                                viewModel.actualizarPrecio()
                                viewModel.updateReserva()
                                formularioViewModel.upedateFormulario()
                                goBack(reservaId)
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFF00F5A0)
                            )
                        ) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showConfirmationDialog = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }



            Text("Detalles de la reserva", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Text("Tipo de vuelo", fontWeight = FontWeight.Bold)
            Text(tipoVuelo?.nombreVuelo ?: "No disponible", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Aeronave", fontWeight = FontWeight.Bold)
            Text(aeronave?.modeloAvion ?: "No disponible", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Origen", fontWeight = FontWeight.Bold)
            Text(ruta?.origen ?: "No disponible", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Destino", fontWeight = FontWeight.Bold)
            Text(ruta?.destino ?: "No disponible", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Pasajeros", fontWeight = FontWeight.Bold)
            Text(uiState.pasajeros.toString(), fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Fecha", fontWeight = FontWeight.Bold)
            Text(formatDateToDMY(fecha.toString()), fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Piloto?",
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = if (tipoCliente) "Sí" else "No",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Licencia", fontWeight = FontWeight.Bold)
            Text(licencia?.descripcion ?: "No aplica", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Modificación", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Text("Información personal", fontWeight = FontWeight.Bold, fontSize = 18.sp)


            OutlinedTextField(
                value = formularioState.nombre,
                onValueChange = onChangeNombre,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )


            OutlinedTextField(
                value = formularioState.apellido,
                onValueChange = { formularioViewModel.onApellidoChange(it) },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )


            OutlinedTextField(
                value = formularioState.correo,
                onValueChange = { formularioViewModel.onCorreoChange(it) },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formatPhoneNumber(formularioState.telefono),
                onValueChange = { newValue ->
                    val digitsOnly = newValue.filter { it.isDigit() }
                    val limitedDigits = if (digitsOnly.length > 10) digitsOnly.substring(0, 10) else digitsOnly
                    formularioViewModel.onTelefonoChange(limitedDigits)
                },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = formularioState.ciudadResidencia,
                onValueChange = { formularioViewModel.onCiudadResidenciaChange(it) },
                label = { Text("Ciudad de residencia") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            FechaPickerField(
                selectedDate = fecha?.toString(),
                onDateSelected = { viewModel.onFechaChange(it) }
            )


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
                            if ((uiState.pasajeros ?: 0) > 1) {
                                onChangePasajeros((uiState.pasajeros ?: 0) - 1)
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if ((uiState.pasajeros
                                        ?: 0) > 1
                                ) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrementar",
                            tint = if ((uiState.pasajeros
                                    ?: 0) > 1
                            ) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Contador de pasajeros
                    Text(
                        text = (uiState.pasajeros ?: 0).toString(),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.width(24.dp),
                        textAlign = TextAlign.Center
                    )

                    // Botón de incremento (sumar 1)
                    IconButton(
                        onClick = {
                            if ((uiState.pasajeros ?: 0) < capacidadMaxima) {
                                onChangePasajeros((uiState.pasajeros ?: 0) + 1)
                            } else {
                                showCapacityAlert.value = true
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if ((uiState.pasajeros ?: 0) < capacidadMaxima)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.errorContainer,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Incrementar",
                            tint = if ((uiState.pasajeros ?: 0) < capacidadMaxima)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Text(
                text = "Máximo: $capacidadMaxima pasajeros",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

        }
    }

}

@SuppressLint("DefaultLocale")
@Composable
fun FechaPickerField(
    selectedDate: String?,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current

    // Configurar la fecha inicial
    val calendar = Calendar.getInstance()
    selectedDate?.let {
        val parts = it.split("-")
        if (parts.size == 3) {
            try {
                calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            } catch (e: NumberFormatException) {
                calendar.time = Date()
            }
        }
    }

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Guardar en formato YYYY-MM-DD internamente
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        setCancelable(true)
    }

    OutlinedTextField(
        value = formatDateToDMY(selectedDate),
        onValueChange = {},
        readOnly = true,
        label = { Text("Fecha del vuelo") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { datePickerDialog.show() }),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Seleccionar fecha",
                modifier = Modifier.clickable(onClick = { datePickerDialog.show() }))
        },
        shape = RoundedCornerShape(16.dp)
    )
}


@Composable
fun InfoRow(
    title: String,
    value: String,
    onChange: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 16.sp, color = Color.Gray)
        onChange?.invoke() // <- aquí se renderiza el contenido adicional
        Spacer(modifier = Modifier.height(8.dp))
    }
}