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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
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
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    viewModel: ReservaViewModel,
    rutaViewModel: RutaViewModel = hiltViewModel(),
    goBack:()->Unit,
    goToFormulario: (Int)-> Unit,


    ){


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rutaUiState by rutaViewModel.uiState.collectAsStateWithLifecycle()
    val tipoVueloUiState by tipoVueloViewModel.uiState.collectAsStateWithLifecycle()


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
        reservaViewModel = viewModel


    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaBodyListScreen(
    uiState: UiState,
    reservaViewModel:ReservaViewModel,
    tipoVueloViewModel: TipoVueloViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    tipoVueloList:List<TipoVueloEntity>,
    rutaList:List<RutaEntity>,
    goToFormulario: (Int)-> Unit,
    rutaUiState: RutaUiState,
    tipoVueloUiState: TipoVueloUiState,
    goBack:()->Unit,
    )
{

   /*val tipoVueloSeleccionado by reservaViewModel.tipoVueloSeleccionadoId.collectAsState()
    val rutaSeleccionadaId by  rutaViewModel.rutaSeleccionadaId.collectAsState()*/

    val idTipoVueloSeleccionado by reservaViewModel.tipoVueloSeleccionadoId.collectAsState()
    val tipoVueloSeleccionado = tipoVueloUiState.tipovuelo.find { it.tipoVueloId == idTipoVueloSeleccionado }

    val idRutaSeleccionada by reservaViewModel.rutaSeleccionadaId.collectAsState()
    val rutaSeleccionada = rutaUiState.rutas.find { it.rutaId == idRutaSeleccionada }


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
    ){innerPadding->
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()

        ) {

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "Tipo de vuelo: ${tipoVueloSeleccionado?.nombreVuelo ?: "No seleccionado"}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = rutaSeleccionada?.let { "Origen: ${it.origen}"} ?: "No seleccionado"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = rutaSeleccionada?.let { "Destino: ${it.destino}"} ?: "No seleccionado"
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (uiState.fecha != null) {
                    Text("Fecha: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(uiState.fecha)}")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = rutaSeleccionada?.let { "Duracion estimada: ${it.duracion}"} ?: "duracion: No disponible"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text =  "Piloto?"
                )

            }


            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { //save()
                    goToFormulario(0)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A80ED),
                    contentColor = Color.White
                )

            ) {
                Text("Continuar reserva")
            }

        }
    }
}

