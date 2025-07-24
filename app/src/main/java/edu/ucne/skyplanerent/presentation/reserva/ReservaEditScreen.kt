package edu.ucne.skyplanerent.presentation.reserva

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel

@Composable
fun ReservaEditScreen(
    reservaId: Int,
    viewModel: ReservaViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    aeronaveViewModel: AeronaveViewModel = hiltViewModel(),
    goBack: (Int) -> Unit
) {
    LaunchedEffect(reservaId) {
        viewModel.selectReserva(reservaId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()
    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val aeronaveUiState by aeronaveViewModel.uiState.collectAsStateWithLifecycle()

    ReservaEditBodyScreen(
        uiState = uiState,
        onChangePasajeros = viewModel::onChangePasajeros,
        save = viewModel::updateReserva,
        goBack = goBack,
        tipoVueloUiState = tipoVueloUiState,
        rutaUiState = rutaUiState,
        aeronaveUiState = aeronaveUiState,
        onChangeRuta = viewModel::onChangeRuta,
        onChangeAeronave = viewModel::categoriaIdChange,
        reservaId = reservaId

    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaEditBodyScreen(
    uiState: UiState,
    reservaId: Int,
    onChangePasajeros: (Int) -> Unit,
    save: () -> Unit,
    goBack: (Int) -> Unit,
    tipoVueloUiState: TipoVueloUiState,
    rutaUiState: RutaUiState,
    aeronaveUiState: AeronaveUiState,
    onChangeRuta: (Int)-> Unit,
    onChangeAeronave:(Int)-> Unit
) {

    val tipoVuelo = tipoVueloUiState.tipovuelo.find { it.tipoVueloId == uiState.tipoVueloId }
    val ruta = rutaUiState.rutas.find { it.rutaId == uiState.rutaId }
    val aeronave = aeronaveUiState.aeronaves.find { it.aeronaveId == uiState.categoriaId }

    val fecha = uiState.fecha
    val tipoCliente = uiState.tipoCliente
    val licencia = uiState.licenciaPiloto

    var showRutaDialog by rememberSaveable { mutableStateOf(false) }
    val selectedAeronave = aeronaveUiState.aeronaves.find { it.aeronaveId == uiState.categoriaId }
    val selectedRuta = rutaUiState.rutas.find { it.rutaId == uiState.rutaId }


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
                onClick = {
                    save()
                    goBack(reservaId)
                },
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
                    containerColor = Color.White // asegúrate de que no esté transparente
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


            Text("Modificar aeronave", fontWeight = FontWeight.Bold)
            AeronavesDropdown(
                aeronaves = aeronaveUiState.aeronaves,
                selectedAeronave = selectedAeronave,
                onAeronaveSelected = { selected ->
                    onChangeAeronave(selected.aeronaveId ?: 0)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Modificar ruta", fontWeight = FontWeight.Bold)
            RutaDropdown(
                rutas = rutaUiState.rutas,
                selectedRuta = selectedRuta,
                onRutaSelected = { selected ->
                    onChangeRuta(selected.rutaId ?: 0)
                }
            )

            ChangeRow("Fecha y hora") { /* Acción cambiar fecha */ }

            ChangeRow("Pasajeros") { /* Acción cambiar pasajeros */ }

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
                            onAeronaveSelected(aeronave) // <- aquí se actualiza el estado en VM
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
