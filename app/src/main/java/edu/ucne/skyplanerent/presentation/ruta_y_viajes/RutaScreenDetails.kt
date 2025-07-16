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



    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        Text(
            text = "Detalles de la ruta",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        ruta?.let {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Columna Izquierda
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Origen",
                        fontSize = 14.sp,
                        color = Color(0xFF5A6B87)
                    )
                    Text(
                        text = it.origen,
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
                        text = "${it.distancia} millas nautics\n(NM)",
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
                        text = it.destino,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Duracion estimada",
                        fontSize = 14.sp,
                        color = Color(0xFF5A6B87)
                    )
                    Text(
                        text = "${it.duracion} hour ${if (it.duracion % 60 != 0) "${it.duracion % 60} minutes" else ""}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } ?: Text("Ruta no encontrada")

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { /* Acción al seleccionar */ },
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
    }
}
