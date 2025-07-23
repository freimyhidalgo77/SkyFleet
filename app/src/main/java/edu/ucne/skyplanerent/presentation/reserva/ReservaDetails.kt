package edu.ucne.skyplanerent.presentation.reserva

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.R
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.navigation.Screen
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.formulario.FormularioViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun ReservaDetailsScreen(
    reservaId:Int,
    viewModel: ReservaViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    aeronaveViewModel: AeronaveViewModel =  hiltViewModel(),
    scope: CoroutineScope,
    goBack:(Int)->Unit,
    goToEdit: (Int)->Unit,
    goToDelete:(Int)->Unit,

    ){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()
    val aeronaveUiState by aeronaveViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(reservaId) {
        viewModel.selectReserva(reservaId)
    }

    ReservaDetailsBodyScreen(
        uiState = uiState,
        scope = scope,
        goBack = goBack,
        goToEdit = goToEdit,
        goToDelete = goToDelete,
        reservaId = reservaId,
        tipoVueloUiState = tipoVueloUiState,
        rutaUiState = rutaUiState,
        aeronaveUiState = aeronaveUiState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaDetailsBodyScreen(
    uiState: UiState,
    scope: CoroutineScope,
    goBack:(Int)->Unit,
    goToEdit: (Int)->Unit,
    goToDelete:(Int)->Unit,
    tipoVueloUiState: TipoVueloUiState,
    rutaUiState: RutaUiState,
    aeronaveUiState: AeronaveUiState,
    reservaId:Int

){

    val ruta = uiState.rutaSeleccionada
    val tipoVuelo = uiState.tipoVueloSeleccionado
    val aeronave = uiState.aeronaveSeleccionada


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Detalles de la reserva",
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()

        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)

            ) {
                items(uiState.reservas) { reserva ->
                    ReservaDetailsRow(
                        reservaId = reservaId,
                        reserva = reserva,
                        ruta = ruta,
                        tipoVuelo = tipoVuelo,
                        aeronave = aeronave,
                        goToEdit = goToEdit,
                        goToDelete = goToDelete,
                        uiState
                    )
                }

            }
        }
    }
}


@Composable
fun ReservaDetailsRow(
    reservaId: Int,
    reserva: ReservaEntity,
    ruta: RutaDTO?,
    tipoVuelo: TipoVueloDTO?,
    aeronave: AeronaveDTO?,
    goToEdit: (Int) -> Unit,
    goToDelete: (Int) -> Unit,
    uiState: UiState,
    rutaViewModel: RutaViewModel = hiltViewModel(),
    formularioViewModel: FormularioViewModel = hiltViewModel(),
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    aeronaveViewModel: AeronaveViewModel =  hiltViewModel(),

    ) {

    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()
    val aeronaveUiState by aeronaveViewModel.uiState.collectAsStateWithLifecycle()

    val formularioUiState by formularioViewModel.uiState.collectAsStateWithLifecycle()

    val tipoVuelo = tipoVueloUiState.tipovuelo.find { it.tipoVueloId == uiState.tipoVueloId }
    val ruta = rutaUiState.rutas.find { it.rutaId == uiState.rutaId }
    val aeronave = aeronaveUiState.aeronaves.find { it.aeronaveId == uiState.categoriaId }

    val fromulario = formularioUiState.formularios.find { it.formularioId == uiState.formularioId }

    val fecha = uiState.fecha
    val tipoCliente = uiState.tipoCliente
    val licencia = uiState.licenciaPiloto

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Reserva NO.#: ${reserva.reservaId}",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Detalles de la reserva", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        InfoRow("Tipo de vuelo", tipoVuelo?.nombreVuelo ?: "No disponible")
        InfoRow("Aeronave", aeronave?.modeloAvion ?: "No disponible")
        InfoRow("Origen", ruta?.origen ?: "No disponible")
        InfoRow("Destino", ruta?.destino ?: "No disponible")
        //InfoRow("Duración", ruta?.duracion ?: "No disponible")
        InfoRow("Pasajeros", reserva.pasajeros.toString())
        InfoRow("Fecha", fecha?.toString() ?: "No seleccionada")
        InfoRow("Piloto", when (tipoCliente) {
            true -> "Sí"
            false -> "No"
            else -> "No especificado"
        })
        InfoRow("Licencia", licencia?.descripcion ?: "No aplica")


        Spacer(modifier = Modifier.height(20.dp))

        Text("Detalles del cliente: ${reserva.pasajeros}", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(120.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { goToEdit(reserva.reservaId ?:0) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0AEDA9),
                    contentColor = Color.White
                )
            ) {
                Text("Modificar reserva")
            }

            Button(
                onClick = { goToDelete(reserva.reservaId?: 0) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFED0A0A),
                    contentColor = Color.White
                )
            ) {
                Text("Cancelar reserva")
            }
        }
    }
}
