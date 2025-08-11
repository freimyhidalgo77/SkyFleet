package edu.ucne.skyplanerent.presentation.aeronave

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.presentation.categoriaaeronave.CategoriaAeronaveUiState
import edu.ucne.skyplanerent.presentation.categoriaaeronave.CategoriaAeronaveViewModel
import kotlinx.coroutines.delay

@Composable
fun AeronaveListScreen(
    viewModel: AeronaveViewModel = hiltViewModel(),
    categoriaViewModel: CategoriaAeronaveViewModel = hiltViewModel(),
    goToAeronave: (Int) -> Unit,
    createAeronave: () -> Unit,
    goBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categoriaUiState by categoriaViewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        delay(180000) // Actualizar cada 3 minutos
        if (uiState.categoriaId != null) {
            viewModel.onEvent(AeronaveEvent.FilterByCategoria(uiState.categoriaId!!))
        } else {
            viewModel.onEvent(AeronaveEvent.GetAeronaves)
        }
    }

    AeronaveListBodyScreen(
        uiState = uiState,
        categoriaUiState = categoriaUiState,
        query = query,
        searchResults = searchResults,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        goToAeronave = goToAeronave,
        onEvent = viewModel::onEvent,
        createAeronave = createAeronave,
        goBack = goBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AeronaveListBodyScreen(
    uiState: AeronaveUiState,
    categoriaUiState: CategoriaAeronaveUiState,
    query: String,
    searchResults: List<AeronaveDTO>,
    onSearchQueryChanged: (String) -> Unit,
    goToAeronave: (Int) -> Unit,
    onEvent: (AeronaveEvent) -> Unit,
    createAeronave: () -> Unit,
    goBack: () -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = {
            if (uiState.categoriaId != null) {
                onEvent(AeronaveEvent.FilterByCategoria(uiState.categoriaId))
            } else {
                onEvent(AeronaveEvent.GetAeronaves)
            }
        }
    )

    val categoriaDescripcion = uiState.categoriaId?.let { id ->
        categoriaUiState.categorias.find { it.categoriaId == id }?.descripcionCategoria
    } ?: "Todas las Aeronaves"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = categoriaDescripcion,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = createAeronave,
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Agregar nueva aeronave",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "Admin Panel",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = { Text("Admin") },
                    selected = false,
                    onClick = goBack
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Flight,
                            contentDescription = "Aeronave",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Blue
                        )
                    },
                    label = { Text("Aeronave") },
                    selected = true,
                    onClick = {}
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            when {
                uiState.isLoading && uiState.aeronaves.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                uiState.aeronaves.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay aeronaves disponibles en este momento",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        item {
                            SearchBar(
                                query = query,
                                onQueryChanged = onSearchQueryChanged
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        val aeronavesToShow = if (query.isNotBlank()) searchResults else uiState.aeronaves

                        items(aeronavesToShow) { aeronave ->
                            AeronaveRow(
                                it = aeronave,
                                goToAeronave = { goToAeronave(aeronave.aeronaveId ?: 0) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = uiState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = MaterialTheme.colorScheme.primary
            )

            if (!uiState.errorMessage.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        label = { Text("Buscar aeronaves por modelo o registración") },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar búsqueda",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}

@Composable
private fun AeronaveRow(
    it: AeronaveDTO,
    goToAeronave: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { goToAeronave(it.aeronaveId ?: 0) }
    ) {
        if (it.imagePath != null) {
            AsyncImage(
                model = it.imagePath,
                contentDescription = "Imagen de ${it.modeloAvion}",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                error = painterResource(id = android.R.drawable.ic_menu_gallery)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AirplanemodeActive,
                contentDescription = "Placeholder de ${it.modeloAvion}",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = it.modeloAvion,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Blue)) {
                        append("Registración: ")
                    }
                    append(it.registracion)
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Blue)) {
                        append("Categoría: ")
                    }
                    append(it.descripcionCategoria)
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = { goToAeronave(it.aeronaveId ?: 0) }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Ver/Editar aeronave",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
    HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
}