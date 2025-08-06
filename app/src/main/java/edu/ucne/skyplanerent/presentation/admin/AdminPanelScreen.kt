package edu.ucne.skyplanerent.presentation.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import edu.ucne.skyplanerent.R
import androidx.compose.ui.geometry.Size
import edu.ucne.skyplanerent.presentation.navigation.Screen
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.ColorFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    adminId: Int,
    navController: NavController,
    goBack: () -> Unit,
    goToPerfil: (Int) -> Unit,
    viewModel: AdminPanelViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error // Rojo para baja actividad
    val midRedColor = Color(0xFFFF5555) // Rojo claro para intermedio bajo
    val neutralColor = Color(0xFFFFA500) // Amarillo para actividad estable
    val midYellowColor = Color(0xFFFFD700) // Amarillo oscuro para intermedio alto
    val successColor = Color(0xFF388E3C) // Verde oscuro para alta actividad

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    icon = {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(R.drawable.admin)
                                .size(24)
                                .build(),
                            contentDescription = "Admin Panel (Activo)",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                    },
                    label = { Text("Admin Panel", fontSize = 12.sp) },
                    selected = true,
                    onClick = { /* Ya estamos en Admin Panel */ }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = { Text("Perfil", fontSize = 12.sp) },
                    selected = false,
                    onClick = { goToPerfil(adminId) }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "Panel de Administrador",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp),
                        color = primaryColor
                    )
                }
                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage ?: "Error desconocido",
                        color = errorColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "Total de rutas", fontSize = 16.sp)
                            Text(
                                text = uiState.rutas.size.toString(),
                                fontSize = 24.sp,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "Aeronaves activas", fontSize = 16.sp)
                            Text(
                                text = uiState.aeronaves.size.toString(),
                                fontSize = 24.sp,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "Usuarios registrados", fontSize = 16.sp)
                            Text(
                                text = uiState.users.size.toString(),
                                fontSize = 24.sp,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "Total de reservas", fontSize = 16.sp)
                            Text(
                                text = uiState.reservas.size.toString(),
                                fontSize = 24.sp,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Tendencias",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Estadísticas de rutas", fontSize = 16.sp)
                        val rutasCount = uiState.rutas.size
                        val aeronavesCount = uiState.aeronaves.size
                        val rutasPorAeronave = if (aeronavesCount > 0) {
                            rutasCount.toFloat() / aeronavesCount.toFloat()
                        } else {
                            0f
                        }
                        val (percentage, percentageText, graphColor, values) = when {
                            rutasPorAeronave < 0.5f -> Quad("-15%", "Muy baja actividad en rutas", errorColor, listOf(6f, 5f, 4f, 3f, 2f, 1f))
                            rutasPorAeronave < 1f -> Quad("-10%", "Baja actividad en rutas", midRedColor, listOf(5f, 4.5f, 4f, 3.5f, 3f, 2.5f))
                            rutasPorAeronave <= 2f -> Quad("0%", "Actividad estable", neutralColor, listOf(5f, 5f, 5f, 5f, 5f, 5f))
                            rutasPorAeronave <= 3f -> Quad("+10%", "Actividad creciente", midYellowColor, listOf(5f, 6f, 4f, 6f, 4f, 5f))
                            else -> Quad("+15%", "Alta actividad en rutas", successColor, listOf(1f, 3f, 5f, 7f, 9f, 11f))
                        }
                        Text(
                            text = percentage,
                            fontSize = 24.sp,
                            color = graphColor,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = percentageText,
                            fontSize = 12.sp,
                            color = graphColor,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Canvas(modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(top = 8.dp)) {
                            val months = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun")
                            val stepWidth = size.width / (values.size - 1)
                            val points = values.mapIndexed { index, value ->
                                val x = index * stepWidth
                                val y = size.height - (value / 15f) * size.height
                                Offset(x, y)
                            }

                            // Dibujar líneas entre los puntos
                            for (i in 0 until points.size - 1) {
                                drawLine(
                                    color = graphColor,
                                    start = points[i],
                                    end = points[i + 1],
                                    strokeWidth = 4f
                                )
                            }

                            // Dibujar puntos en cada valor
                            points.forEach { point ->
                                drawCircle(
                                    color = graphColor,
                                    radius = 8f,
                                    center = point
                                )
                            }

                            // Dibujar etiquetas de los meses
                            months.forEachIndexed { index, month ->
                                drawContext.canvas.nativeCanvas.apply {
                                    drawText(
                                        month,
                                        index * stepWidth,
                                        size.height,
                                        android.graphics.Paint().apply {
                                            color = android.graphics.Color.GRAY
                                            textSize = 24f
                                            textAlign = android.graphics.Paint.Align.CENTER
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Estadísticas de reservas", fontSize = 16.sp)
                        val reservasCount = uiState.reservas.size
                        val (percentage, percentageText, graphColor, values) = when {
                            reservasCount < 3 -> Quad("-15%", "Últimos 30 días -15%", errorColor, listOf(6f, 5f, 4f, 3f, 2f, 1f))
                            reservasCount <= 7 -> Quad("-5%", "Últimos 30 días -5%", midRedColor, listOf(5f, 4.5f, 4f, 3.5f, 3f, 2.5f))
                            reservasCount <= 15 -> Quad("0%", "Últimos 30 días estable", neutralColor, listOf(5f, 5f, 5f, 5f, 5f, 5f))
                            reservasCount <= 25 -> Quad("+5%", "Últimos 30 días +5%", midYellowColor, listOf(5f, 6f, 4f, 6f, 4f, 5f))
                            else -> Quad("+10%", "Últimos 30 días +10%", successColor, listOf(1f, 3f, 5f, 7f, 9f, 11f))
                        }
                        Text(
                            text = percentage,
                            fontSize = 24.sp,
                            color = graphColor,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = percentageText,
                            fontSize = 12.sp,
                            color = graphColor,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Canvas(modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(top = 8.dp)) {
                            val months = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun")
                            val barWidth = size.width / values.size
                            values.forEachIndexed { index, value ->
                                val barHeight = (value / 15f) * size.height
                                drawRect(
                                    color = graphColor,
                                    topLeft = Offset(index * barWidth + barWidth / 4, size.height - barHeight),
                                    size = Size(barWidth / 2, barHeight)
                                )
                            }

                            // Dibujar etiquetas de los meses debajo del Canvas
                            months.forEachIndexed { index, month ->
                                drawContext.canvas.nativeCanvas.apply {
                                    drawText(
                                        month,
                                        index * barWidth + barWidth / 2,
                                        size.height + 20f,
                                        android.graphics.Paint().apply {
                                            color = android.graphics.Color.GRAY
                                            textSize = 24f
                                            textAlign = android.graphics.Paint.Align.CENTER
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Seleccionar una opción",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        onClick = { navController.navigate(Screen.CategoriaAeronaveList) },
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(R.drawable.aeronave)
                                    .size(24)
                                    .build(),
                                contentDescription = "Aeronaves",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                            )
                            Text(text = "Gestionar aeronaves", fontSize = 16.sp)
                        }
                    }
                    Card(
                        onClick = { navController.navigate(Screen.RutaList) },
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(R.drawable.ruta)
                                    .size(24)
                                    .build(),
                                contentDescription = "Rutas",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                            )
                            Text(text = "Gestionar rutas", fontSize = 16.sp)
                        }
                    }
                }
            }

            item {
                Card(
                    onClick = { navController.navigate(Screen.TipoVueloList) },
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(R.drawable.tipovuelo)
                                .size(32)
                                .build(),
                            contentDescription = "Tipos de Vuelo",
                            modifier = Modifier.size(32.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                        Text(text = "Gestionar tipos de vuelo", fontSize = 16.sp)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// Definición de Quad en el mismo archivo
data class Quad(
    val percentage: String,
    val percentageText: String,
    val color: Color,
    val values: List<Float>
)
