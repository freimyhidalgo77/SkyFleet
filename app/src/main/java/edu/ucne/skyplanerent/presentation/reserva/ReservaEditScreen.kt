package edu.ucne.skyplanerent.presentation.reserva

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ReservaEditScreen(
    reservaId:Int,
    viewModel: ReservaViewModel = hiltViewModel(),
    goBack:(Int)->Unit

){

    LaunchedEffect(reservaId)
    {
        viewModel.selectReserva(reservaId)

    }
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    ReservaEditBodyScreen(
        uiState = uiState.value,
        onChangePasajeros = viewModel::onChangePasajeros,
        save = viewModel::saveReserva,
        goBack = goBack

    )
}


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ReservaEditBodyScreen(
    uiState: UiState,
    /*onChangeAeronave:(String)-> Unit,
    onChangeRuta:(String)-> Unit,
    onChangeFecha:(String)-> Unit,*/
    onChangePasajeros:(Int)-> Unit,
    save:()->Unit,
    goBack: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Modificar reserva",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White

                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )

        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp)

        ) {
            /*OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Aeronave") },
                value = uiState.pasajeros.toString(),
                onValueChange = {
                    val pasajero = it.toIntOrNull() ?: 0
                    onChangePasajeros(pasajero)
                }
            )*/

            /*OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Fecha") },
                value = uiState.pasajeros.toString(),
                onValueChange = {
                    val pasajero = it.toIntOrNull() ?: 0
                    onChangePasajeros(pasajero)
                }
            )*/

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Pasajeros") },
                value = uiState.pasajeros.toString(),
                onValueChange = {
                    val pasajero = it.toIntOrNull() ?: 0
                    onChangePasajeros(pasajero)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Ruta") },
                value = uiState.rutaId.toString(),
                onValueChange = {
                    val ruta = it.toIntOrNull() ?: 0
                    // onChangeRuta(ruta)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedButton(
                    onClick = {
                        save()
                        goBack(0)

                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)

                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Guardar")
                    Text("Guardar")

                }



                Spacer(modifier = Modifier.height(16.dp))
                uiState.successMessage?.let { menssage ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        content = {
                            Text(
                                text = menssage,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Green
                            )
                        }
                    )
                }

                uiState.errorMessage?.let { menssage ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        content = {
                            Text(
                                text = menssage,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Red
                            )
                        }
                    )
                }
            }
        }
    }
}




