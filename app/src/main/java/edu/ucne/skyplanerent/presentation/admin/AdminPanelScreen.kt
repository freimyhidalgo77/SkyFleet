package edu.ucne.skyplanerent.presentation.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import edu.ucne.skyplanerent.R
import edu.ucne.skyplanerent.presentation.navigation.Screen

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
    val errorColor = MaterialTheme.colorScheme.error
    val neutralColor = Color(0xFFFFA500) // Naranja para rango intermedio
    val successColor = Color(0xFF388E3C) // Verde oscuro para alta actividad

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.admin),
                            contentDescription = "Admin Panel (Activo)",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
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
                        val (percentage, percentageText, graphColor) = when {
                            rutasPorAeronave < 1f -> Triple("-10%", "Baja actividad en rutas", errorColor)
                            rutasPorAeronave in 1f..2f -> Triple("0%", "Actividad estable", neutralColor)
                            else -> Triple("+15%", "Alta actividad en rutas", successColor)
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
                            val values = when {
                                rutasPorAeronave < 1f -> listOf(6f, 5f, 4f, 3f, 2f, 1f)
                                rutasPorAeronave in 1f..2f -> listOf(5f, 5f, 5f, 5f, 5f, 5f)
                                else -> listOf(1f, 3f, 5f, 7f, 9f, 11f)
                            }
                            val barWidth = size.width / values.size
                            values.forEachIndexed { index, value ->
                                val barHeight = (value / 15f) * size.height
                                drawRect(
                                    color = graphColor,
                                    topLeft = Offset(index * barWidth + barWidth / 4, size.height - barHeight),
                                    size = Size(barWidth / 2, barHeight)
                                )
                            }

                            months.forEachIndexed { index, month ->
                                drawContext.canvas.nativeCanvas.apply {
                                    drawText(
                                        month,
                                        index * barWidth,
                                        size.height,
                                        android.graphics.Paint().apply {
                                            color = android.graphics.Color.GRAY
                                            textSize = 24f
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
                        val (percentage, percentageText, graphColor) = when {
                            reservasCount < 5 -> Triple("-5%", "Últimos 30 días -5%", errorColor)
                            reservasCount in 5..15 -> Triple("0%", "Últimos 30 días estable", neutralColor)
                            else -> Triple("+10%", "Últimos 30 días +10%", successColor)
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
                            val values = when {
                                reservasCount < 5 -> listOf(6f, 5f, 4f, 3f, 2f, 1f)
                                reservasCount in 5..15 -> listOf(5f, 5f, 5f, 5f, 5f, 5f)
                                else -> listOf(1f, 3f, 5f, 7f, 9f, 11f)
                            }
                            val barWidth = size.width / values.size
                            values.forEachIndexed { index, value ->
                                val barHeight = (value / 15f) * size.height
                                drawRect(
                                    color = graphColor,
                                    topLeft = Offset(index * barWidth + barWidth / 4, size.height - barHeight),
                                    size = Size(barWidth / 2, barHeight)
                                )
                            }

                            months.forEachIndexed { index, month ->
                                drawContext.canvas.nativeCanvas.apply {
                                    drawText(
                                        month,
                                        index * barWidth,
                                        size.height,
                                        android.graphics.Paint().apply {
                                            color = android.graphics.Color.GRAY
                                            textSize = 24f
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
                            Icon(
                                painter = painterResource(id = R.drawable.aeronave),
                                contentDescription = "Aeronaves",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
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
                            Icon(
                                painter = painterResource(id = R.drawable.ruta),
                                contentDescription = "Rutas",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
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
                        Icon(
                            painter = painterResource(id = R.drawable.tipovuelo),
                            contentDescription = "Tipos de Vuelo",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
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