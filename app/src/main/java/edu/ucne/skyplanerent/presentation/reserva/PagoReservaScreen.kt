package edu.ucne.skyplanerent.presentation.reserva

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.navigation.Screen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.formulario.FormularioUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.formulario.FormularioViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun PagoReservaListScreen(
    pagoReservaId:Int,
    /*TipoVueloviewModel: TipoVueloViewModel = hiltViewModel(),
    RutaviewModel: RutaViewModel = hiltViewModel(),
    tipoVueloId:Int,
    RutaId:Int,*/
    tipoVueloList: List<TipoVueloEntity>,
    rutaList: List<RutaEntity>,
    viewModel: ReservaViewModel,
    rutaViewModel: RutaViewModel = hiltViewModel(),
    aeronaveViewModel: AeronaveViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    formularioViewModel: FormularioViewModel = hiltViewModel(),
    goBack:()->Unit

){

    LaunchedEffect(pagoReservaId) {
        if (pagoReservaId > 0) {
            formularioViewModel.selectedFormulario(pagoReservaId)
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()
    val aeronaevUiState by aeronaveViewModel.uiState.collectAsStateWithLifecycle()
    val formularioUiState by formularioViewModel.uiState.collectAsStateWithLifecycle()


    PagoReservaBodyListScreen(
        uiState = uiState,
        //scope = scope,
        tipoVueloList = tipoVueloList,
        rutaList = rutaList,
        /* onCreate = onCreate,
         onEdit = onEdit,
         onDelete = onDelete,*/
        PagoReservaId = pagoReservaId,
        goBack = goBack,
        rutaUiState = rutaUiState,
        tipoVueloUiState = tipoVueloUiState,
        reservaViewModel = viewModel,
        aeronaveUiState = aeronaevUiState,
        formularioUiState = formularioUiState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoReservaBodyListScreen(
    uiState: UiState,
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    reservaViewModel: ReservaViewModel,
    // scope: CoroutineScope,
    tipoVueloList:List<TipoVueloEntity>,
    rutaList:List<RutaEntity>,
    PagoReservaId:Int,
    rutaUiState: RutaUiState,
    aeronaveUiState:AeronaveUiState,
    tipoVueloUiState: TipoVueloUiState,
    formularioUiState: FormularioUiState,
    goBack:()->Unit

) {
    val navController = rememberNavController()

    val idTipoVueloSeleccionado by reservaViewModel.tipoVueloSeleccionadoId.collectAsState()
    val tipoVueloSeleccionado =
        tipoVueloUiState.tipovuelo.find { it.tipoVueloId == idTipoVueloSeleccionado }

    val idRutaSeleccionada by reservaViewModel.rutaSeleccionadaId.collectAsState()
    val rutaSeleccionada = rutaUiState.rutas.find { it.rutaId == idRutaSeleccionada }

    val idAeronaveSeleccionada by reservaViewModel.tipoAeronaveSeleccionadaId.collectAsState()
    val aeronaveSeleccionada = aeronaveUiState.aeronaves.find { it.aeronaveId == idAeronaveSeleccionada }

    val fechaVuelo by reservaViewModel.fechaSeleccionada.collectAsState()

    // Valores base
    val duracionVuelo = rutaUiState.duracionEstimada?.toDouble() ?: 0.0
   // val costoXHora = aeronaveUiState.CostoXHora ?: 0.0

    val costoXHora: Int =  150 //esta variable es mientrastanto para probar

    val tipoCliente by reservaViewModel.tipoCliente.collectAsState()

// Calcular
    val tarifaBase = duracionVuelo * costoXHora
    val impuesto = tarifaBase * 0.10
    val precioTotal = tarifaBase + impuesto


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Pre-Reserva",
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start
        ) {

            item {
                Text(
                    text = "Tipo de vuelo: ${tipoVueloSeleccionado?.nombreVuelo ?: "No seleccionado"}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Text(
                    text = rutaSeleccionada?.let { "Origen: ${it.origen}" } ?: "No seleccionado",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Text(
                    text = rutaSeleccionada?.let { "Destino: ${it.destino}" } ?: "No seleccionado",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Text(
                    text = "Aeronave seleccionada: ${aeronaveSeleccionada?.modeloAvion ?: "No seleccionado"}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            fechaVuelo?.let { fecha ->
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fechaFormateada = formato.format(fecha)

                item {
                    Text(
                        text = "Fecha seleccionada: $fechaFormateada",
                        fontSize = 16.sp,
                        color = Color(0xFF0A80ED),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    Text(
                        text = rutaSeleccionada?.let { "Duración estimada: ${it.duracion}" }
                            ?: "Duración: No disponible",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                item {
                    Text(
                        text = "Nombre ${formularioUiState.nombre} Apellido ${formularioUiState.apellido}",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                item {
                    Text(
                        text = "Pasaporte ${formularioUiState.pasaporte}",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }


                item {
                    Text(
                        text = "Cantidad pasajeros ${formularioUiState.cantidadPasajeros}",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                item {
                    Text(text = "¿Es Piloto?", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        text = when (tipoCliente) {
                            true -> "Sí"
                            false -> "No"
                            else -> "No especificado"
                        },
                        fontSize = 16.sp,
                        color = Color.Gray

                    )
                }


                item {
                    Text(
                        text = "Desgloce de precio: $tarifaBase",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                item {
                    Text(
                        text = "Impuesto: $impuesto",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                item {
                    Text(
                        text = "Precio total: $precioTotal",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }


                item {
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {

                            val rutaId = rutaSeleccionada?.rutaId ?: return@Button
                            val tipoVueloId = tipoVueloSeleccionado?.tipoVueloId ?: return@Button
                            val aeronaveId = aeronaveSeleccionada?.aeronaveId ?: return@Button
                            //val fechaFormateada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fechaVuelo ?: Date())
                            val tipoCliente = uiState?.tipoCliente ?: return@Button

                            reservaViewModel.guardarReserva(
                                rutaId = rutaId,
                                tipoVueloId = tipoVueloId,
                                aeronaveId = aeronaveId,
                                //fecha = uiState.fecha,
                                tarifaBase = tarifaBase,
                                impuesto = impuesto,
                                precioTotal = precioTotal,
                                tipoCliente = tipoCliente
                            )

                            goBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0A80ED),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Realizar pago")
                    }

                }
            }
        }
    }
}




