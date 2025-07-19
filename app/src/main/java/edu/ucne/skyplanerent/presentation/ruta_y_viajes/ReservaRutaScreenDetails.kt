package edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.presentation.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaRutaScreenDetails(
    rutaId: Int?,
    viewModel: RutaViewModel = hiltViewModel(),
    onSelectRuta: (Int) -> Unit,
    goBack:() -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(rutaId) {
        rutaId?.let {
            if (it > 0) {
                viewModel.onEvent(RutaEvent.GetRuta(it))
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.NavigateUp -> goBack()
                is UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    ReservaRutaDetailsBodyScreen(
        uiState = uiState,
        goBack = goBack,
        onDelete = {
            viewModel.onEvent(RutaEvent.Delete)
        },
        onSelectRuta = { onSelectRuta(rutaId ?: 0) },
        snackbarHostState = snackbarHostState,
        rutaId = rutaId!!
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaRutaDetailsBodyScreen(
    uiState: RutaUiState,
    viewModel: RutaViewModel = hiltViewModel(),
    goBack: () -> Unit,
    onDelete: () -> Unit,
    onSelectRuta: (Int) -> Unit,
    snackbarHostState: SnackbarHostState,
    rutaId:Int


) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        Text(
            text = "Detalles de la ruta",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black, // mejor contraste
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .wrapContentWidth(align = androidx.compose.ui.Alignment.CenterHorizontally)
        )

        if (uiState.rutaId != null) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Columna Izquierda
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Origen",
                        fontSize = 14.sp,
                        color = Color(0xFF5A6B87)
                    )
                    Text(
                        text = uiState.origen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Distancia",
                        fontSize = 14.sp,
                        color = Color(0xFF5A6B87)
                    )
                    Text(
                        text = "${uiState.distancia} millas nautics\n(NM)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Columna Derecha
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Destino",
                        fontSize = 14.sp,
                        color = Color(0xFF5A6B87)
                    )
                    Text(
                        text = uiState.destino,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Duración estimada",
                        fontSize = 14.sp,
                        color = Color(0xFF5A6B87)
                    )
                    Text(
                        text = "${uiState.duracionEstimada} hour ${if (uiState.duracionEstimada % 60 != 0) "${uiState.duracionEstimada % 60} minutes" else ""}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    //viewModel.seleccionarRuta(rutaId)
                    //onSelectRuta(rutaId)
                    goBack()},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF339AFF),
                    contentColor = Color.White
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp)
            ) {
                Text("Seleccionar ruta")
            }

        } else {
            Text(
                text = "Ruta no encontrada",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        uiState.errorMessage?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

