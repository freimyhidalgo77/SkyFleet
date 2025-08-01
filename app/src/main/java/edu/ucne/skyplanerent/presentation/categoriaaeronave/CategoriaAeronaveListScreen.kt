package edu.ucne.skyplanerent.presentation.categoriaaeronave

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import edu.ucne.skyplanerent.R
import edu.ucne.skyplanerent.data.local.entity.CategoriaAeronaveEntity

@Composable
fun CategoriaAeronaveListScreen(
    viewModel: CategoriaAeronaveViewModel = hiltViewModel(),
    goToCategoria: (Int) -> Unit, // Para navegar a detalles
    createCategoria: () -> Unit, // Para crear nueva categoría (sin ID)
    onEdit: (Int) -> Unit, // Para editar categoría (con ID)
    deleteCategoria: ((CategoriaAeronaveEntity) -> Unit)? = null,
    goBack: () -> Unit,
    goToAdminPanel: () -> Unit, // Modified to accept adminId
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CategoriaAeronaveListBodyScreen(
        uiState = uiState,
        goToCategoria = goToCategoria,
        createCategoria = createCategoria,
        onEdit = onEdit,
        deleteCategoria = { categoria ->
            viewModel.onEvent(CategoriaAeronaveEvent.CategoriaIdChange(categoria.categoriaId ?: 0))
            viewModel.onEvent(CategoriaAeronaveEvent.Delete)
        },
        goBack = goBack,
        goToAdminPanel = goToAdminPanel
        )
}

@Composable
private fun CategoriaAeronaveRow(
    it: CategoriaAeronaveEntity,
    goToCategoria: (Int) -> Unit, // Para detalles
    createCategoria: () -> Unit, // Para crear nueva categoría
    onEdit: (Int) -> Unit, // Para editar categoría
    deleteCategoria: (CategoriaAeronaveEntity) -> Unit
) {
    val showDeleteDialog = remember { mutableStateOf(false) } // Estado para controlar el diálogo

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { goToCategoria(it.categoriaId ?: 0) }, // Navegar a detalles
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            it.imagePath?.let { path ->
                AsyncImage(
                    model = path,
                    contentDescription = "Imagen de ${it.descripcionCategoria}",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } ?: Spacer(modifier = Modifier.size(100.dp))

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = it.descripcionCategoria,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onEdit(it.categoriaId ?: 0) }) { // Botón de edición usa onEdit con categoriaId
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { showDeleteDialog.value = true }) { // Mostrar diálogo al pulsar eliminar
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false }, // Cerrar diálogo al intentar salir
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar la categoría ${it.descripcionCategoria}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteCategoria(it) // Ejecutar eliminación si se confirma
                        showDeleteDialog.value = false // Cerrar diálogo
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog.value = false } // Cerrar diálogo sin eliminar
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaAeronaveListBodyScreen(
    uiState: CategoriaAeronaveUiState,
    goToCategoria: (Int) -> Unit,
    createCategoria: () -> Unit, // Para crear nueva categoría
    onEdit: (Int) -> Unit, // Para editar categoría
    deleteCategoria: (CategoriaAeronaveEntity) -> Unit,
    goBack: () -> Unit,
    goToAdminPanel: () -> Unit, // Modified to lambda for passing adminId
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Categorías de Aeronaves",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { createCategoria() }) { // Crear nueva categoría
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Nuevo",
                            tint = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
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
                            painterResource(id = R.drawable.admin),
                            contentDescription = "Admin Panel",
                            modifier = Modifier.size(24.dp),
                            tint = if (!false) MaterialTheme.colorScheme.onSurface else Color.Unspecified
                        )
                    },
                    label = { Text("Admin") },
                    selected = false,
                    onClick = goBack
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.aeronave),
                            contentDescription = "Aeronave",
                            modifier = Modifier.size(24.dp),
                            tint = if (true) Color.Blue else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = { Text("Aeronave") },
                    selected = true,
                    onClick = {}
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(uiState.categorias) { categoria ->
                    CategoriaAeronaveRow(
                        it = categoria,
                        goToCategoria = { categoriaId -> goToCategoria(categoriaId) },
                        createCategoria = { createCategoria() },
                        onEdit = { categoriaId -> onEdit(categoriaId) },
                        deleteCategoria = deleteCategoria
                    )
                }
            }
        }
    }
}