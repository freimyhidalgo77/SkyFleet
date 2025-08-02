package edu.ucne.skyplanerent.presentation.ruta_y_viajes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.persistableBundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.reserva.ReservaViewModel
import edu.ucne.skyplanerent.presentation.reserva.UiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun PreReservaListScreen(
    preReservaId:Int,
    tipoVueloList: List<TipoVueloEntity>,
    rutaList: List<RutaEntity>,
    aeronaevViewModel: AeronaveViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    viewModel: ReservaViewModel,
    rutaViewModel: RutaViewModel = hiltViewModel(),
    goBack:(Int)->Unit,
    goToFormulario: (Int)-> Unit,


    ){


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()
    val aeronaevUiState by aeronaevViewModel.uiState.collectAsStateWithLifecycle()


    /*LaunchedEffect(Unit) {
        if (rutaUiState.rutas.isEmpty()) {
            rutaViewModel.getRutas()
        }
    }*/

    ReservaBodyListScreen(
        uiState = uiState,
        // scope = scope,
        tipoVueloList = tipoVueloList,
        rutaList = rutaList,
        goToFormulario = goToFormulario,
        goBack = goBack,
        rutaUiState = rutaUiState,
        tipoVueloUiState = tipoVueloUiState,
        aeronaveUiState = aeronaevUiState,
        reservaViewModel = viewModel,
        preReservaId = preReservaId

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaBodyListScreen(
    preReservaId: Int,
    uiState: UiState,
    reservaViewModel: ReservaViewModel,
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    tipoVueloList: List<TipoVueloEntity>,
    rutaList: List<RutaEntity>,
    goToFormulario: (Int) -> Unit,
    aeronaveUiState: AeronaveUiState,
    rutaUiState: RutaUiState,
    tipoVueloUiState: TipoVueloUiState,
    goBack: (Int) -> Unit,
) {
    val idTipoVueloSeleccionado by reservaViewModel.tipoVueloSeleccionadoId.collectAsState()
    val tipoVueloSeleccionado =
        tipoVueloUiState.tipovuelo.find { it.tipoVueloId == idTipoVueloSeleccionado }

    val idRutaSeleccionada by reservaViewModel.rutaSeleccionadaId.collectAsState()
    val rutaSeleccionada = rutaUiState.rutas.find { it.rutaId == idRutaSeleccionada }

    val idAeronaveSeleccionada by reservaViewModel.tipoAeronaveSeleccionadaId.collectAsState()
    val aeronaveSeleccionada =
        aeronaveUiState.aeronaves.find { it.aeronaveId == idAeronaveSeleccionada }

    val fechaVuelo by reservaViewModel.fechaSeleccionada.collectAsState()
    val tipoCliente by reservaViewModel.tipoCliente.collectAsState()
    val reservaUiState by reservaViewModel.uiState.collectAsStateWithLifecycle()
    val licenciaSeleccionada = reservaUiState.licenciaPiloto


    val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    navigationIcon = {
                        IconButton(onClick = { goBack(preReservaId) }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver atrás")
                        }
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
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    text = "Detalles del vuelo",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                Column(modifier = Modifier.padding(10.dp)) {

                    }

                Text(
                    text = "Tipo de vuelo",
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)

                )
                Text(
                    text = tipoVueloSeleccionado?.nombreVuelo ?: "No seleccionado",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                    Text(
                        text = "Origen",
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = rutaSeleccionada?.origen ?: "No seleccionado",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Destino",
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = rutaSeleccionada?.destino ?: "No seleccionado",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                Spacer(modifier = Modifier.height(12.dp))

                    // Aeronave
                    Text(
                        text = "Aeronave",
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = aeronaveSeleccionada?.modeloAvion ?: "No seleccionado",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                Spacer(modifier = Modifier.height(12.dp))

                    // Fecha
                    fechaVuelo?.let { fecha ->
                        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val fechaFormateada = formato.format(fecha)

                        Text(
                            text = "Fecha",
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = fechaFormateada,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                Spacer(modifier = Modifier.height(12.dp))

                    // Hora
                    Text(
                        text = "Tiempo",
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "10:00 AM - 12:00 PM", // Esto debería venir en los datos
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                Spacer(modifier = Modifier.height(12.dp))

                    // Duración
                    Text(
                        text = "Duración estimada",
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = rutaSeleccionada?.let { "${it.duracion} minutos" }
                            ?: "No disponible",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Piloto
                    Text(
                        text = "Piloto?",
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = when (tipoCliente) {
                            true -> "Sí"
                            false -> "No"
                            else -> "No especificado"
                        },
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Licencia (solo si es piloto)
                    if (tipoCliente == true) {
                        Text(
                            text = "Licencia:",
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = reservaUiState.licenciaPiloto?.descripcion ?: "No especificada",
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {  idAeronaveSeleccionada?.let { goToFormulario(it) }  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A80ED),
                        contentColor = Color.White
                    )
                ) {
                    Text("Continuar reserva", fontSize = 16.sp)
                }
            }
        }
    }
}



