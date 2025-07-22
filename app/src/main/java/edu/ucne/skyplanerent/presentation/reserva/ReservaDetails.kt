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
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun ReservaDetailsScreen(
    reservaId:Int,
    viewModel: ReservaViewModel = hiltViewModel(),
    rutaViewModel: RutaViewModel = hiltViewModel(),
    scope: CoroutineScope,
    goBack:()->Unit,
    goToEdit: (Int)->Unit,
    goToDelete:(Int)->Unit,

    ){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rutauiState by rutaViewModel.uiState.collectAsStateWithLifecycle()


    ReservaDetailsBodyScreen(
        uiState = uiState,
        scope = scope,
        goBack = goBack,
        goToEdit = goToEdit,
        goToDelete = goToDelete,
        reservaId = reservaId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaDetailsBodyScreen(
    uiState: UiState,
    scope: CoroutineScope,
    goBack:()->Unit,
    goToEdit: (Int)->Unit,
    goToDelete:(Int)->Unit,
    reservaId:Int

){
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
                    ReservaDetailsRow(reservaId, reserva, goToEdit, goToDelete)
                }
            }
        }
    }
}


@Composable
fun ReservaDetailsRow(
    reservaId: Int,
    reserva: ReservaEntity,
    goToEdit: (Int) -> Unit,
    goToDelete: (Int) -> Unit,

) {
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

        Text(text = "Detalles de la reserva", fontWeight = FontWeight.Bold)

        Text("Hora y fecha: ${reserva.fecha}", fontSize = 16.sp)
        Text("Origen: ${reserva.rutaId}", fontSize = 16.sp)
        Text("Destino: ${reserva.rutaId}", fontSize = 16.sp)
        Text("Aeronave: ${reserva.categoriaId}", fontSize = 16.sp)
        Text("Duracion del vuelo: ${reserva.categoriaId}", fontSize = 16.sp)

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
