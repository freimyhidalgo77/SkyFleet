package edu.ucne.skyplanerent.presentation.rutayviajes

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.presentation.reserva.ReservaViewModel
import edu.ucne.skyplanerent.presentation.rutayviajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.rutayviajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo.TipoVueloViewModel
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import edu.ucne.skyplanerent.presentation.navigation.BottomNavItem
import edu.ucne.skyplanerent.presentation.navigation.Screen
import edu.ucne.skyplanerent.presentation.reserva.ReservaEvent
import edu.ucne.skyplanerent.presentation.reserva.UiState
import edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo.TipoLicencia
import edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo.TipoVueloUiState
import edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo.tipoLicenciaFromDescripcion
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
    goBack: () -> Unit,
    navController: NavController

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
        onReserva = { fecha, comprobante, metodoPago ->

            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser == null) {
                // Mostrar error o redirigir a login
                return@Vuelos_RutasBodyListScreen
            }

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
                    licenciaPiloto = licenciaSeleccionada,
                    userId = currentUser.uid,
                    comprobante = comprobante,
                    metodoPago = metodoPago?:""

                )
                reservaViewModel.guardarReserva(
                    rutaId = selectedRuta?.rutaId!!,
                    tipoVueloId = selectedTipoVuelo?.tipoVueloId!!,
                    aeronaveId = selectedAeronave?.aeronaveId!!,
                    //fecha = fecha,
                    tarifaBase = 1000.0,
                    impuesto = 0.0,
                    precioTotal = 0.0,
                    tipoCliente = soyPiloto ?: false,
                    pasajero = 0,
                    metodoPago = metodoPago?:"",
                    comprobante = comprobante?:"",
                    formularioId = reservaUiState.formularioId?:0

                )//aqui se pasa a reserva en el metodo saveReserva(reserva:ReservaEntity)
            }
        },
        goBackDetails = goBackDetails,
        goTopreReserva = goTopreReserva,
        goToRuta = goToRuta,
        reservaViewModel = reservaViewModel,
        reservaUiState = reservaUiState,
        goBack = goBack,
        navController = navController

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
    onReserva: (Date, String?, String?) -> Unit,
    goBackDetails: (Int) -> Unit,
    goToRuta: (Int) -> Unit,
    goBack:()-> Unit,
    goTopreReserva: (Int)-> Unit,
    reservaUiState:UiState,
    navController:NavController


) {
    var fechaSeleccionada by remember { mutableStateOf<Date?>(null) }
    fechaSeleccionada = reservaUiState.fecha
    var selectedAeronave by remember { mutableStateOf<AeronaveDTO?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }


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


    val isLoading = uiState.rutas.isEmpty() || vueloUiState.tipovuelo.isEmpty()

    val items = listOf(
        BottomNavItem("Inicio", Icons.Default.Home, Screen.Home),
        BottomNavItem("Perfil", Icons.Default.Person, Screen.Perfil),
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route


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
                navigationIcon = {
                    IconButton(onClick = { goBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },

        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route.toString(),
                        onClick = {
                            if (currentRoute != item.route.toString()) {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Home) { inclusive = false }
                                }
                            }
                        }
                    )
                }
            }
        }


    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {


            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {

                item {
                    Text(
                        text = "Tipos de vuelo",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Para los tipos de vuelo
                        items(vuelosAMostrar) { vuelo ->
                            val isSelected = vuelo.tipoVueloId == selectedTipoVuelo?.tipoVueloId
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF2196F3) else Color(0xFFBBDEFB)
                                ),
                                elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 2.dp),
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(60.dp)
                                    .clickable {
                                        if (isSelected) {
                                            selectedTipoVuelo = null
                                            reservaViewModel.seleccionarTipoVuelo(0)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Se ha cancelado la selección del tipo de vuelo")
                                            }// 0 para indicar no selección
                                        } else {
                                            selectedTipoVuelo = vuelo
                                            reservaViewModel.seleccionarTipoVuelo(vuelo.tipoVueloId ?: 0)
                                        }
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
                                        color = if (isSelected) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                val idTipoVueloSeleccionado = ideSeleccionado
                if (idTipoVueloSeleccionado != null && idTipoVueloSeleccionado > 0) {
                    item {
                        val tipoVueloSeleccionado = vueloUiState.tipovuelo.find { it.tipoVueloId == idTipoVueloSeleccionado }
                        if (tipoVueloSeleccionado != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Tipo Vuelo Seleccionado: ${tipoVueloSeleccionado.descripcionTipoVuelo}",
                                    fontSize = 16.sp,
                                    color = Color(0xFF0A80ED)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(
                                    onClick = {
                                        selectedTipoVuelo = null
                                        reservaViewModel.seleccionarTipoVuelo(0)
                                    }
                                ) {
                                    Text("Cancelar", color = Color.Red)
                                }
                            }
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    text = "Ruta seleccionada: ${rutaSeleccionada.origen} → ${rutaSeleccionada.destino}",
                                    fontSize = 16.sp,
                                    color = Color(0xFF0A80ED)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(
                                    onClick = {
                                        selectedRuta = null
                                        reservaViewModel.seleccionarRuta(0)
                                    }
                                ) {
                                    Text("Cancelar", color = Color.Red)
                                }
                            }
                        }
                    }
                }

                items(rutasAMostrar) { ruta ->
                    val isSelected = ruta.rutaId == selectedRuta?.rutaId
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable {
                                if (isSelected) {
                                    selectedRuta = null
                                    reservaViewModel.seleccionarRuta(0)// 0 para indicar no selección
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Se ha cancelado la selección de la ruta")
                                    }
                                } else {
                                    selectedRuta = ruta
                                    reservaViewModel.seleccionarRuta(ruta.rutaId!!)
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF2196F3) else Color(0xFFE3F2FD)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ruta: ${ruta.rutaId}",
                                color = if (isSelected) Color.White else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${ruta.origen} → ${ruta.destino}",
                                fontSize = 16.sp,
                                color = if (isSelected) Color.White else Color.Black
                            )
                            Text(
                                text = "Distancia: ${ruta.distancia}",
                                color = if (isSelected) Color.White else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Duracion: ${ruta.duracion} hour ${if (uiState.duracionEstimada % 60 != 0) "${uiState.duracionEstimada % 60} minutes" else ""}",
                                color = if (isSelected) Color.White else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
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

                var seleccionUsuario by remember { mutableStateOf<Boolean?>(null) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            seleccionUsuario = true
                            soyPiloto = true
                            mostrarLicencias = true
                            reservaViewModel.seleccionarTipoCliente(true)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (seleccionUsuario == true) Color(0xFF64B5F6) else Color.LightGray
                        )
                    ) {
                        Text("Soy piloto")
                    }

                    Button(
                        onClick = {
                            seleccionUsuario = false
                            soyPiloto = false
                            mostrarLicencias = false
                            licenciaSeleccionada = null
                            reservaViewModel.seleccionarTipoCliente(false)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (seleccionUsuario == false) Color(0xFF64B5F6) else Color.LightGray
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
                        fechaSeleccionada != null &&
                        (soyPiloto != true || licenciaSeleccionada != null) && selectedRuta != null && selectedTipoVuelo != null
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        //Guarda los datos seleccionados antes de navegar
                        selectedTipoVuelo?.let { reservaViewModel.seleccionarTipoVuelo(it.tipoVueloId ?: 0,) }
                        selectedRuta?.let { reservaViewModel.seleccionarRuta(it.rutaId ?: 0) }

                        ReservaEvent.save
                        goTopreReserva(0)
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
    ).apply {
        // Establecer la fecha mínima como el día actual
        datePicker.minDate = calendario.timeInMillis
    }

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