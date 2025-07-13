package edu.ucne.skyplanerent.presentation.ruta_y_viajes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel


@Composable
fun RutaScreenDetails(
    rutaId: Int,
    goBack:()->Unit,
    viewModel: RutaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect (rutaId) {
        viewModel.findRuta(rutaId)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Detalles la de Ruta", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Origen: ${uiState.origen}")
        Text(text = "Destino: ${uiState.destino}")
        Text(text = "Distancia: ${uiState.distancia} km")
        Text(text = "Duración estimada: ${uiState.duracionEstimada} min")
    }
}
