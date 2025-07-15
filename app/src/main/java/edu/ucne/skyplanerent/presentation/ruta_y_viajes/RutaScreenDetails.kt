package edu.ucne.skyplanerent.presentation.ruta_y_viajes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel


@Composable
fun RutaScreenDetails(
    rutaId: Int,
    goBack: () -> Unit,
    viewModel: RutaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(rutaId) {
        viewModel.findRuta(rutaId)
    }

    val ruta = uiState.rutas.find { it.RutaId == rutaId }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Detalles de la Ruta", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        ruta?.let {
            Text(text = "Origen: ${it.origen}")
            Text(text = "Destino: ${it.destino}")
            Text(text = "Distancia: ${it.distancia} km")
            Text(text = "Duración estimada: ${it.duracion} min")
        } ?: Text("Ruta no encontrada")


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0A80ED),
                contentColor = Color.White
            )
        ) {
            Text("Seleccionar ruta")
        }

    }

}
