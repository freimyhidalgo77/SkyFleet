package edu.ucne.skyplanerent.presentation.reserva

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.formulario.FormularioViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel
import java.util.Calendar
import java.util.Date



@Composable
fun ReservaEditScreen(
    reservaId: Int,
    viewModel: ReservaViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    aeronaveViewModel: AeronaveViewModel = hiltViewModel(),
    formularioViewModel: FormularioViewModel = hiltViewModel(),
    goBack: (Int) -> Unit
) {
    LaunchedEffect(reservaId) {
        viewModel.selectReserva(reservaId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()
    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val aeronaveUiState by aeronaveViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.reservaSeleccionada?.formularioId) {
        uiState.reservaSeleccionada?.formularioId?.let { formularioId ->
            formularioViewModel.selectedFormulario(formularioId)
        }
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
        aeronaveUiState = aeronaveUiState,
        onChangeRuta = viewModel::onChangeRuta,
        onChangeAeronave = viewModel::categoriaIdChange,
        onChangeTipoVuelo = viewModel::onChangeTipoVuelo,
        onDateSelected = viewModel::onFechaChange,
        reservaId = reservaId,
        formularioViewModel = formularioViewModel,
        onChangeNombre = formularioViewModel::onNombreChange


    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaEditBodyScreen(
    uiState: UiState,
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
    formularioViewModel:FormularioViewModel = hiltViewModel()
) {


    val reserva = uiState.reservaSeleccionada ?: return

    val formularioState by formularioViewModel.uiState.collectAsStateWithLifecycle()

    // Buscar el formulario asociado a la reserva
    val formulario = formularioState.formularios.find { it.formularioId == reserva.formularioId }

    //val reserva = uiState.reservaSeleccionada ?: return

    val tipoVuelo = tipoVueloUiState.tipovuelo.find { it.tipoVueloId == reserva.tipoVueloId }
    val ruta = rutaUiState.rutas.find { it.rutaId == reserva.rutaId }
    val aeronave = aeronaveUiState.aeronaves.find { it.aeronaveId == reserva.categoriaId }

    val fecha = uiState.fecha
    val tipoCliente = uiState.tipoCliente
    val licencia = uiState.licenciaPiloto

    var showRutaDialog by rememberSaveable { mutableStateOf(false) }
    val selectedAeronave = aeronaveUiState.aeronaves.find { it.aeronaveId == uiState.categoriaId }
    val selectedRuta = rutaUiState.rutas.find { it.rutaId == uiState.rutaId }
    val selectedTipoVuelo = tipoVueloUiState.tipovuelo.find { it.tipoVueloId == uiState.tipoVueloId }

    var showConfirmationDialog by remember { mutableStateOf(false) }


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
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver atrás")
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
                Text("Confirmar cambios", fontWeight = FontWeight.Bold)
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
                                save()
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
            Text(fecha?.toString() ?: "No seleccionada", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Piloto", fontWeight = FontWeight.Bold)
            Text(
                when (tipoCliente) {
                    true -> "Sí"
                    false -> "No"
                    else -> "No especificado"
                },
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text("Licencia", fontWeight = FontWeight.Bold)
            Text(licencia?.descripcion ?: "No aplica", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Modificación", fontWeight = FontWeight.Bold, fontSize = 18.sp)


            Text("Información personal", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            // Campo para editar nombre
            OutlinedTextField(
                value = formularioState?.nombre ?: "",
                onValueChange = onChangeNombre,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo para editar apellido
            OutlinedTextField(
                value = formularioState?.apellido ?: "",
                onValueChange = { formularioViewModel.onApellidoChange(it) },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo para editar correo
            OutlinedTextField(
                value = formularioState?.correo ?: "",
                onValueChange = { formularioViewModel.onCorreoChange(it) },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo para editar teléfono
            OutlinedTextField(
                value = formularioState?.telefono ?: "",
                onValueChange = { formularioViewModel.onTelefonoChange(it) },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(8.dp))



            FechaPickerField(
                selectedDate = fecha?.toString(),
                onDateSelected = { viewModel.onFechaChange(it) }
            )

            PasajerosDropdown(
                selectedPasajeros = uiState.pasajeros?:0,
                onPasajerosSelected = { onChangePasajeros(it) }
            )

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AeronavesDropdown(
    aeronaves: List<AeronaveDTO>,
    selectedAeronave: AeronaveDTO?,
    onAeronaveSelected: (AeronaveDTO) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = selectedAeronave?.modeloAvion ?: "Seleccionar aeronave",
            onValueChange = {}, // read-only
            readOnly = true,
            label = { Text("Modelo de Aeronave") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (aeronaves.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No hay aeronaves disponibles") },
                    onClick = { expanded = false }
                )
            } else {
                aeronaves.forEach { aeronave ->
                    DropdownMenuItem(
                        text = { Text(aeronave.modeloAvion) },
                        onClick = {
                            onAeronaveSelected(aeronave)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaDropdown(
    rutas: List<RutaDTO>,
    selectedRuta: RutaDTO?,
    onRutaSelected: (RutaDTO) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = selectedRuta?.let { "${it.origen} → ${it.destino}" } ?: "Seleccionar ruta",
            onValueChange = {},
            readOnly = true,
            label = { Text("Ruta") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (rutas.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No hay rutas disponibles") },
                    onClick = { expanded = false }
                )
            } else {
                rutas.forEach { ruta ->
                    DropdownMenuItem(
                        text = { Text("${ruta.origen} → ${ruta.destino}") },
                        onClick = {
                            onRutaSelected(ruta)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoVueloDropdown(
    tipoVuelo: List<TipoVueloDTO>,
    selectedTipoVuelo: TipoVueloDTO?,
    onTipoVueloSelected: (TipoVueloDTO) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = selectedTipoVuelo?.let { "${it.nombreVuelo}" } ?: "Seleccionar tipo vuelo",
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo Vuelo") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (tipoVuelo.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No hay tipos de vuelo disponibles") },
                    onClick = { expanded = false }
                )
            } else {
                tipoVuelo.forEach { tipoVuelo ->
                    DropdownMenuItem(
                        text = { Text("${tipoVuelo.nombreVuelo}") },
                        onClick = {
                            onTipoVueloSelected(tipoVuelo)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun FechaPickerField(
    selectedDate: String?,
    onDateSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Configurar la fecha inicial
    val calendar = Calendar.getInstance()
    selectedDate?.let {
        val parts = it.split("-")
        if (parts.size == 3) {
            try {
                calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            } catch (e: NumberFormatException) {
                // Usar fecha actual si el formato no es válido
                calendar.time = Date()
            }
        }
    }

    // Crear el DatePickerDialog
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        setCancelable(true)
    }

    // Manejar el click para mostrar el diálogo
    val clickHandler = {
        showDialog = true
        datePickerDialog.show()
    }

    OutlinedTextField(
        value = selectedDate ?: "Seleccionar fecha",
        onValueChange = {},
        readOnly = true,
        label = { Text("Fecha del vuelo") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = clickHandler),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Seleccionar fecha",
                modifier = Modifier.clickable(onClick = clickHandler)
            )
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasajerosDropdown(
    selectedPasajeros: Int,
    onPasajerosSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = selectedPasajeros.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Cantidad de pasajeros") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            (1..10).forEach { count ->
                DropdownMenuItem(
                    text = { Text("$count") },
                    onClick = {
                        onPasajerosSelected(count)
                        expanded = false
                    }
                )
            }
        }
    }
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


@Composable
fun ChangeRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 16.sp)
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(50)
        ) {
            Text("Cambiar")
        }
    }
}
