package edu.ucne.skyplanerent.presentation.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.navigation.Screen
import edu.ucne.skyplanerent.presentation.reserva.ReservaViewModel
import kotlinx.coroutines.delay

@Composable
fun AdminPanelScreen(
    navController: NavController,
    goBack: () -> Unit,
    aeronaveViewModel: AeronaveViewModel = hiltViewModel(),
    reservaViewModel: ReservaViewModel = hiltViewModel(),
    goToPerfil: () -> Unit
) {
    val aeronaveState by aeronaveViewModel.uiState.collectAsState()
    val reservaState by reservaViewModel.uiState.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Panel de Administrador",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )
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
                        Text(text = "Total de vuelos", fontSize = 16.sp)
                        Text(
                            text = "1,234", // Valor estático por ahora
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
                            text = aeronaveState.aeronaves.size.toString(),
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
                            text = "567", // Valor estático por ahora
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
                            text = reservaState.reservas.size.toString(),
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
                    Text(text = "Estadísticas de vuelos", fontSize = 16.sp)
                    Text(
                        text = "+12%",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "Últimos 30 días +12%",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Canvas(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 8.dp)) {
                        val months = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun")
                        val staticValues = listOf(10f, 12f, 8f, 15f, 7f, 13f) // Valores estáticos
                        val barWidth = size.width / staticValues.size
                        var lastX = 0f
                        var lastY = size.height - (staticValues[0] / 15f) * size.height

                        staticValues.forEachIndexed { index, value ->
                            val x = index * barWidth
                            val y = size.height - (value / 15f) * size.height
                            if (index > 0) {
                                drawLine(
                                    color = primaryColor,
                                    start = Offset(lastX, lastY),
                                    end = Offset(x, y),
                                    strokeWidth = 2.dp.toPx()
                                )
                            }
                            lastX = x
                            lastY = y
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
                    Text(
                        text = "-5%",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "Últimos 30 días -5%",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Canvas(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 8.dp)) {
                        val months = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun")
                        val staticValues = listOf(6f, 9f, 5f, 12f, 4f, 10f) // Valores estáticos
                        val barWidth = size.width / staticValues.size
                        staticValues.forEachIndexed { index, value ->
                            val barHeight = (value / 15f) * size.height
                            drawRect(
                                color = errorColor,
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(painter = painterResource(id = R.drawable.aeronave), contentDescription = "Aeronaves", modifier = Modifier.size(24.dp))
                        Text(text = "Gestionar aeronaves", fontSize = 16.sp)
                    }
                }
                Card(
                    onClick = { navController.navigate(Screen.RutaList) },
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(painter = painterResource(id = R.drawable.ruta), contentDescription = "Rutas", modifier = Modifier.size(24.dp))
                        Text(text = "Gestionar rutas", fontSize = 16.sp)
                    }
                }
            }
        }

        item {
            Card(
                onClick = { navController.navigate(Screen.TipoVueloList) },
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(painter = painterResource(id = R.drawable.tipovuelo), contentDescription = "Tipos de Vuelo", modifier = Modifier.size(32.dp))
                    Text(text = "Gestionar tipos de vuelo", fontSize = 16.sp)
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { /* Navegar a Admin Panel */ }) {
                        Icon(painter = painterResource(id = R.drawable.admin), contentDescription = "Admin Panel (Activo)", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    }
                    Text(text = "Admin Panel", fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = goToPerfil) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Perfil")
                    }
                    Text(text = "Perfil", fontSize = 16.sp)
                }
            }
        }
    }
}