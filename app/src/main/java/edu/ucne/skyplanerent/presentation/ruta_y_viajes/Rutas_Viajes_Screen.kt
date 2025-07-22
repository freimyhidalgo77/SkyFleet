package edu.ucne.skyplanerent.presentation.ruta_y_viajes

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.presentation.reserva.ReservaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import androidx.compose.material.icons.filled.CalendarToday
import edu.ucne.skyplanerent.presentation.reserva.ReservaEvent
import edu.ucne.skyplanerent.presentation.reserva.UiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoLicencia
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.tipoLicenciaFromDescripcion
//import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.tipoLicenciaFromString
import kotlinx.coroutines.launch


@Composable
fun Rutas_Viajes_Screen(
    reservaViewModel: ReservaViewModel,
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    aeronaveViewModel: AeronaveViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    scope: CoroutineScope,
    goBackDetails: (Int) -> Unit,
    goTopreReserva: (Int)-> Unit,
    goToRuta: (Int) -> Unit,


    ) {

    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()
    val aeronavesUiState by aeronaveViewModel.uiState.collectAsStateWithLifecycle()
    val reservaUiState by reservaViewModel.uiState.collectAsStateWithLifecycle()

    var selectedTipoVuelo by remember { mutableStateOf<TipoVueloDTO?>(null) }
    var selectedRuta by remember { mutableStateOf<RutaDTO?>(null) }
    var selectedAeronave by remember { mutableStateOf<AeronaveDTO?>(null) }
    var soyPiloto by remember { mutableStateOf<Boolean?>(null) }
    var licenciaSeleccionada by remember { mutableStateOf<String?>(null) }


    Vuelos_RutasBodyListScreen(
        uiState = rutaUiState,
        uiStateA = aeronavesUiState,
        vueloUiState = tipoVueloUiState,
        tiposDeVuelo = tipoVueloUiState.tipovuelo,
        scope = scope,
        onReserva = { fecha ->
            if (selectedTipoVuelo != null && selectedRuta != null && selectedAeronave != null) {
                val reserva = ReservaEntity(
                    tipoVueloId = selectedTipoVuelo!!.tipoVueloId,
                    rutaId = selectedRuta!!.rutaId,
                    fecha = fecha,
                    pasajeros = 1,
                    impuesto = 0.0,
                    tarifa = 1000.0,
                    precioTotal = 0.0,
                    categoriaId = selectedAeronave!!.aeronaveId,
                    tipoCliente = soyPiloto ?: false,
                    licenciaPiloto = licenciaSeleccionada

                )
                reservaViewModel.guardarReserva(
                    rutaId = selectedRuta?.rutaId!!,
                    tipoVueloId = selectedTipoVuelo?.tipoVueloId!!,
                    aeronaveId = selectedAeronave?.aeronaveId!!,
                    //fecha = fecha,
                    tarifaBase = 1000.0,
                    impuesto = 0.0,
                    precioTotal = 0.0,
                    tipoCliente = soyPiloto ?: false
                )//aqui se pasa a reserva en el metodo saveReserva(reserva:ReservaEntity)
            }
        },
        goBackDetails = goBackDetails,
        goTopreReserva = goTopreReserva,
        goToRuta = goToRuta,
        reservaViewModel = reservaViewModel,
        reservaUiState = reservaUiState

    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Vuelos_RutasBodyListScreen(
    uiState: RutaUiState,
    vueloUiState: TipoVueloUiState,
    uiStateA: AeronaveUiState,
    tiposDeVuelo: List<TipoVueloDTO>,
    scope: CoroutineScope,
    rutaViewModel: RutaViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    reservaViewModel: ReservaViewModel,
    onReserva: (Date) -> Unit,
    goBackDetails: (Int) -> Unit,
    goToRuta: (Int) -> Unit,
    goTopreReserva: (Int)-> Unit,
    reservaUiState:UiState


    ) {
    var fechaSeleccionada by remember { mutableStateOf<Date?>(null) }
    fechaSeleccionada = reservaUiState.fecha
    var selectedAeronave by remember { mutableStateOf<AeronaveDTO?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }


   /* val licencias = listOf(
        "PPL - Piloto Privado",
        "CPL - Piloto Comercial",
        "ATPL - Piloto de Transporte de Línea Aérea",
        "IR - Habilitación de Vuelo por Instrumentos",
        "ME - Habilitación Multimotor",
        "Turboprop - Habilitación Turboprop",
        "Jet Type Rating - Habilitación Jet"
    )*/

    val licencias = TipoLicencia.values().toList()


    var soyPiloto by remember { mutableStateOf<Boolean?>(null) }
    soyPiloto = reservaUiState.tipoCliente

    var licenciaSeleccionada by remember { mutableStateOf<TipoLicencia?>(null) }

    //Manejo de licencia
    var mostrarLicencias by remember { mutableStateOf(false) }
    var expandedLicencia by remember { mutableStateOf(false) }

    var selectedTipoVuelo by remember { mutableStateOf<TipoVueloDTO?>(null) }
    var selectedRuta by remember { mutableStateOf<RutaDTO?>(null) }



    val idSeleccionado by reservaViewModel.rutaSeleccionadaId.collectAsState()
    val rutasAMostrar = idSeleccionado?.let { id ->
        if (id > 0) {
            uiState.rutas.filter { it.rutaId == id }
        } else {
            uiState.rutas
        }
    } ?: uiState.rutas


    val ideSeleccionado by reservaViewModel.tipoVueloSeleccionadoId.collectAsState()
    val vuelosAMostrar = ideSeleccionado?.let { id ->
        if (id > 0) {
            vueloUiState.tipovuelo.filter { it.tipoVueloId == id }
        } else {
            vueloUiState.tipovuelo
        }
    } ?:   vueloUiState.tipovuelo


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tipos de vuelos y destinos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            item {
                Text(
                    text = "Tipos de vuelo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(vuelosAMostrar) { vuelo ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB)),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier
                                .width(160.dp)
                                .height(60.dp)
                                .clickable {
                                    selectedTipoVuelo = vuelo
                                    reservaViewModel.seleccionarTipoVuelo(vuelo.tipoVueloId ?: 0)
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = vuelo.nombreVuelo,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            val idTipoVueloSeleccionado = idSeleccionado
            if (idTipoVueloSeleccionado != null && idTipoVueloSeleccionado > 0) {
                item {
                    val tipoVueloSeleccionado = vueloUiState.tipovuelo.find { it.tipoVueloId == idTipoVueloSeleccionado }

                    if (tipoVueloSeleccionado != null) {
                        Text(
                            text = "Tipo Vuelo Seleccionado: ${tipoVueloSeleccionado.descripcionTipoVuelo}",
                            fontSize = 16.sp,
                            color = Color(0xFF0A80ED),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Rutas disponibles",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            val idRutaSeleccionada = idSeleccionado
            if (idRutaSeleccionada != null && idRutaSeleccionada > 0) {
                item {
                    val rutaSeleccionada = uiState.rutas.find { it.rutaId == idRutaSeleccionada }

                    if (rutaSeleccionada != null) {
                        Text(
                            text = "Ruta seleccionada: ${rutaSeleccionada.origen} → ${rutaSeleccionada.destino}",
                            fontSize = 16.sp,
                            color = Color(0xFF0A80ED),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            items(rutasAMostrar) { ruta ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable {
                            selectedRuta = ruta
                            reservaViewModel.seleccionarRuta(ruta.rutaId!!)
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ruta: ${ruta.rutaId}",
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${ruta.origen} → ${ruta.destino}",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Distancia: ${ruta.distancia}",
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Duracion: ${ruta.duracion} hour ${if (uiState.duracionEstimada % 60 != 0) "${uiState.duracionEstimada % 60} minutes" else ""}",
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                    }
                }
            }

            item {
                FechaSelector(
                    fechaSeleccionada = fechaSeleccionada,
                    onFechaSeleccionada = { nuevaFecha ->
                        fechaSeleccionada = nuevaFecha
                        reservaViewModel.seleccionarFecha(nuevaFecha)
                    }

                )
            }

            item {
                Text(
                    text = "Tipo de cliente",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            soyPiloto = true
                            mostrarLicencias = true
                            reservaViewModel.seleccionarTipoCliente(true)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (soyPiloto == true) Color(0xFF64B5F6) else Color.LightGray
                        )
                    ) {
                        Text("Soy piloto")
                    }

                    Button(
                        onClick = {
                            //reservaUiState.tipoCliente = false
                            soyPiloto = false
                            mostrarLicencias = false
                            licenciaSeleccionada = null
                            reservaViewModel.seleccionarTipoCliente(false)

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (soyPiloto == false) Color(0xFF64B5F6) else Color.LightGray
                        )
                    ) {
                        Text("Necesito un piloto")
                    }
                }
            }


            if (mostrarLicencias) {
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedLicencia,
                        onExpandedChange = { expandedLicencia = !expandedLicencia },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        OutlinedTextField(
                            value = licenciaSeleccionada?.toString() ?: "Seleccionar licencia",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Licencia de piloto") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLicencia)
                            },
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedLicencia,
                            onDismissRequest = { expandedLicencia = false }
                        ) {
                            licencias.forEach { licencia ->
                                DropdownMenuItem(
                                    text = { Text(licencia.toString()) },
                                    onClick = {
                                        licenciaSeleccionada = licencia
                                        expandedLicencia = false

                                       // reservaViewModel.seleccionarLicenciaPiloto(licencia)

                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {

                AeronaveDropdown(
                    aeronaves = uiStateA.aeronaves,
                    selectedAeronave = selectedAeronave,
                    onAeronaveSelected = { aeronave ->
                        val licenciaEnum = tipoLicenciaFromDescripcion(aeronave.licencia)
                        if (soyPiloto == true && licenciaSeleccionada != null) {
                            if (licenciaEnum == licenciaSeleccionada) {
                                selectedAeronave = aeronave
                                reservaViewModel.seleccionarTipoAeronave(aeronave.aeronaveId ?: 0)
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "La aeronave seleccionada requiere una licencia diferente: ${aeronave.licencia}",
                                        withDismissAction = true
                                    )
                                }
                            }
                        } else if (soyPiloto != true) {
                            selectedAeronave = aeronave
                            reservaViewModel.seleccionarTipoAeronave(aeronave.aeronaveId ?: 0)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Debes seleccionar una licencia antes de elegir una aeronave.",
                                    withDismissAction = true
                                )
                            }
                        }
                    }
                )
            }

            item {
                val puedeContinuar = selectedAeronave != null &&
                        /*fechaSeleccionada != null &&
                        (soyPiloto != true || licenciaSeleccionada != null) && */selectedRuta != null && selectedTipoVuelo != null
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        //Guarda los datos seleccionados antes de navegar
                        selectedTipoVuelo?.let { reservaViewModel.seleccionarTipoVuelo(it.tipoVueloId ?: 0,) }
                        selectedRuta?.let { reservaViewModel.seleccionarRuta(it.rutaId ?: 0) }

                        ReservaEvent.save

                        goTopreReserva(0) // Navega a la próxima pantalla
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
                    Text("Continuar")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AeronaveDropdown(
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
            value = selectedAeronave?.modeloAvion?: "Seleccionar aeronave",
            onValueChange = {},
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
                    onClick = {

                    }
                )
            }

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


@Composable
fun FechaSelector(
    fechaSeleccionada: Date?,
    onFechaSeleccionada: (Date) -> Unit
) {
    val contexto = LocalContext.current
    val calendario = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaTexto = fechaSeleccionada?.let { dateFormat.format(it) } ?: ""

    val datePickerDialog = DatePickerDialog(
        contexto,
        { _, year, month, day ->
            val nuevaFecha = Calendar.getInstance()
            nuevaFecha.set(year, month, day)
            onFechaSeleccionada(nuevaFecha.time)
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedTextField(
        value = fechaTexto,
        onValueChange = {},
        readOnly = true,
        label = { Text("Fecha del vuelo") },
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(Icons.Filled.CalendarToday, contentDescription = "Seleccionar fecha")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { datePickerDialog.show() }
    )

}
