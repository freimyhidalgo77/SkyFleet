package edu.ucne.skyplanerent

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.ucne.skyplanerent.data.local.entity.UserRegisterAccount
import edu.ucne.skyplanerent.data.repository.UserRepository
import edu.ucne.skyplanerent.presentation.navigation.BottomNavItem
import edu.ucne.skyplanerent.presentation.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToReserva: () -> Unit,
    onNavigateToRutasViajes: () -> Unit,
    onNavigateToPeril: () -> Unit,
    navController: NavController,
    userRepository: UserRepository,
    currentUserEmail: String?
) {
    val items = listOf(
        BottomNavItem("Reservas", Icons.Default.Book, Screen.Reserva),
        BottomNavItem("Aronaves", Icons.Default.AirplanemodeActive, Screen.CategoriaAeronaveReservaList),
        BottomNavItem("Rutas y Viajes", Icons.Default.Map, Screen.Rutasyviajes),
        BottomNavItem("Perfil", Icons.Default.Person, Screen.Perfil),
    )

    val user = remember { mutableStateOf<UserRegisterAccount?>(null) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    LaunchedEffect(currentUserEmail) {
        if (currentUserEmail != null) {
            user.value = userRepository.getUserByEmail(currentUserEmail)
        }
    }

    Scaffold(
        topBar = {},
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route.toString(),
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Home) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        top = 0.dp,  // Cero padding superior
                        bottom = innerPadding.calculateBottomPadding(),
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                    )
                ),
            verticalArrangement = Arrangement.Top,
            contentPadding = PaddingValues(0.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.logoskyfleet),
                        contentDescription = "Logo SkyFleet",
                        modifier = Modifier
                            .width(150.dp)
                            .padding(top = 1.dp)
                    )
                    // Card con imagen C172

                    AnimatedAircraftCarousel(navController)

                    // Texto de bienvenida
                    Text(
                        text = "¡Bienvenido ${user.value?.nombre ?: "Freimy"}!",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .fillMaxWidth()
                    )

                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text =  "Experimente la libertad de volar. " +
                            "Alquile un avion ligero para viajes personales o de negocios, " +
                            "eligiendo su propio horario y destino.",
                    fontSize = 15.sp,
                )

                Spacer(modifier = Modifier.height(15.dp))

            }
        }
    }
}

@Composable
fun AnimatedAircraftCarousel(navController: NavController) {
    val aircraftImages = listOf(
        R.drawable.c172welcome,
        R.drawable.turboprop,
        R.drawable.cirrussr22,
        R.drawable.cessna750x
    )

    val aircraftTitles = listOf(
        "Mis reservas",
        "Explorar Rutas",
        "Reservar vuelo",
        "Nuestra flota de aviones"
    )

    val pagerState = rememberPagerState(pageCount = { aircraftImages.size })

    // Auto-scroll animation
    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000) // Change image every 3 seconds
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1) % pagerState.pageCount,
                animationSpec = tween(durationMillis = 1000)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(horizontal = 16.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            Card(
                onClick = {
                    when (page) {
                        0 -> navController.navigate(Screen.Reserva)
                        1 -> navController.navigate(Screen.Rutasyviajes)
                        2 -> navController.navigate(Screen.Reserva)
                        3 -> navController.navigate(Screen.CategoriaAeronaveReservaList)
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = aircraftImages[page]),
                        contentDescription = aircraftTitles[page],
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Text(
                        text = aircraftTitles[page],
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.5f))
                    )
                }
            }
        }

        // Dots indicator
        Row(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(aircraftImages.size) { index ->
                val color = if (pagerState.currentPage == index)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                        .padding(2.dp)
                )
                if (index != aircraftImages.size - 1) Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}
