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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo.TipoVueloViewModel
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun PagoReservaListScreen(
    /*TipoVueloviewModel: TipoVueloViewModel = hiltViewModel(),
    RutaviewModel: RutaViewModel = hiltViewModel(),
    tipoVueloId:Int,
    rutaId:Int,*/
    tipoVueloList: List<TipoVueloEntity>,
    rutaList: List<RutaEntity>,
    viewModel: ReservaViewModel= hiltViewModel(),
    scope: CoroutineScope,
    onCreate:()-> Unit,
    onEdit:(Int)-> Unit,
    onDelete:(Int)-> Unit

){

    /*LaunchedEffect(rutaId, tipoVueloId){
        TipoVueloviewModel.uiState.value.tipovuelo
        RutaviewModel.uiState.value.rutaId

    }*/

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PagoReservaBodyListScreen(
        uiState = uiState,
        scope = scope,
        tipoVueloList = tipoVueloList,
        rutaList = rutaList,
        onCreate = onCreate,
        onEdit = onEdit,
        onDelete = onDelete
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoReservaBodyListScreen(
    uiState: UiState,
    scope: CoroutineScope,
    tipoVueloList:List<TipoVueloEntity>,
    rutaList:List<RutaEntity>,
    onCreate:()-> Unit,
    onEdit:(Int)-> Unit,
    onDelete:(Int)-> Unit

){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Resumen de la reserva",
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
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)

            ) {
                items(uiState.reservas) { reserva ->
                    val tipoVuelo = tipoVueloList.find { it.vueloId == reserva.tipoVueloId }
                    val ruta = rutaList.find { it.rutaId == reserva.rutaId }

                    // Solo si ambos existen, los mostramos
                    if (tipoVuelo != null && ruta != null) {
                        PagoReservaRow(reserva, tipoVuelo, ruta, onEdit, onDelete)
                    }
                }
            }
        }
    }
}

@Composable
fun PagoReservaRow(
    reserva: ReservaEntity,
    tipoVuelo: TipoVueloEntity,
    ruta: RutaEntity,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit

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

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Tipo de vuelo: ${tipoVuelo.descripcionTipoVuelo}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface

                    )

                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Ruta: ${reserva.rutaId}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface

                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

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

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Tiempo: ${ruta.duracionEstimada}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface

                    )
                )

              Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Desgloce de precios ",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.width(20.dp))


                Text(
                    text = "Costo del vuelo" +
                            "${reserva.tarifa} ",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Impueto" +
                            "${reserva.impuesto}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Precio total" +
                            "${reserva.precioTotal}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

               /* Text(
                    text = "Metodo de pago ",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )*/

            }
        }
    }
}




