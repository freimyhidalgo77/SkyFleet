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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun PreReservaListScreen(
    preReservaId:Int,
    tipoVueloList: List<TipoVueloEntity>,
    rutaList: List<RutaEntity>,
    viewModel: ReservaViewModel = hiltViewModel(),
    goBack:()->Unit,
    goToFormulario: (Int)-> Unit,


    ){

    /*LaunchedEffect(RutaId, tipoVueloId){
        TipoVueloviewModel.uiState.value.tipovuelo
        RutaviewModel.uiState.value.RutaId

    }*/

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReservaBodyListScreen(
        uiState = uiState,
        // scope = scope,
        tipoVueloList = tipoVueloList,
        rutaList = rutaList,
        goToFormulario = goToFormulario,
        goBack = goBack

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaBodyListScreen(
    uiState: UiState,
    tipoVueloList:List<TipoVueloEntity>,
    rutaList:List<RutaEntity>,
    goToFormulario: (Int)-> Unit,
    goBack:()->Unit,

    ){
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
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)

            ) {
                items(uiState.reservas) { reserva ->
                    val tipoVuelo = tipoVueloList.find { it.vueloId == reserva.tipoVueloId }
                    val ruta = rutaList.find { it.rutaId == reserva.rutaId }

                    // Solo si ambos existen, los mostramos
                    if (tipoVuelo != null && ruta != null) {
                        PreReservaRow(reserva, tipoVuelo, ruta)
                    }
                }
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

@Composable
fun PreReservaRow(
    reserva: ReservaEntity,
    tipoVuelo: TipoVueloEntity,
    ruta:RutaEntity,


    ) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Column(
                modifier = Modifier.weight(5f),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Detalles del vuelo:",
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                )

                Text(
                    text = "Tipo de vuelo: ${tipoVuelo.descripcionTipoVuelo}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface

                    )

                )

                Text(
                    text = "Ruta: ${reserva.rutaId}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface

                    )
                )


                Text(
                    text = "Fecha del vuelo: ${
                        reserva.fecha?.let {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                        } ?: "No especificada"
                    }",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                /*  Text(
                      text = "Hora: ${ruta.hora}",
                      style = androidx.compose.ui.text.TextStyle(
                          fontSize = 18.sp,
                          color = MaterialTheme.colorScheme.onSurface

                      )
                  )*/


                Text(
                    text = "Tiempo: ${ruta.duracionEstimada}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface

                    )
                )

            }
        }
    }

}


