package edu.ucne.skyplanerent.presentation.aeronave

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.data.repository.AeronaveRepository
import edu.ucne.skyplanerent.presentation.UiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class AeronaveViewModel @Inject constructor(
    private val aeronaveRepository: AeronaveRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(AeronaveUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // Variables para búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<AeronaveDTO>>(emptyList())
    val searchResults: StateFlow<List<AeronaveDTO>> = _searchResults.asStateFlow()

    init {
        getAeronaves()
        viewModelScope.launch {
            _searchQuery
                .debounce(600)
                .distinctUntilChanged()
                .mapLatest { query ->
                    filterAeronaves(query)
                }
                .collectLatest { filtered ->
                    _searchResults.value = filtered
                }
        }
    }

    fun onEvent(event: AeronaveEvent) {
        when (event) {
            AeronaveEvent.GetAeronaves -> getAeronaves()
            AeronaveEvent.LimpiarErrorMessageEstadoIdChange -> limpiarErrorMessageEstadoId()
            AeronaveEvent.LimpiarErrorMessageModeloAvionChange -> limpiarErrorMessageModeloAvion()
            AeronaveEvent.LimpiarErrorMessageDescripcionCategoriaChange -> limpiarErrorMessageDescripcionCategoria()
            AeronaveEvent.LimpiarErrorMessageRegistracionChange -> limpiarErrorMessageRegistracion()
            AeronaveEvent.LimpiarErrorMessageCostoXHoraChange -> limpiarErrorMessageCostoXHora()
            AeronaveEvent.LimpiarErrorMessageDescripcionAeronaveChange -> limpiarErrorMessageDescripcionAeronave()
            AeronaveEvent.LimpiarErrorMessageVelocidadMaximaChange -> limpiarErrorMessageVelocidadMaxima()
            AeronaveEvent.LimpiarErrorMessageDescripcionMotorChange -> limpiarErrorMessageDescripcionMotor()
            AeronaveEvent.LimpiarErrorMessageCapacidadCombustibleChange -> limpiarErrorMessageCapacidadCombustible()
            AeronaveEvent.LimpiarErrorMessageConsumoXHoraChange -> limpiarErrorMessageConsumoXHora()
            AeronaveEvent.LimpiarErrorMessagePesoChange -> limpiarErrorMessagePeso()
            AeronaveEvent.LimpiarErrorMessageRangoChange -> limpiarErrorMessageRango()
            AeronaveEvent.LimpiarErrorMessageCapacidadPasajerosChange -> limpiarErrorMessageCapacidadPasajeros()
            AeronaveEvent.LimpiarErrorMessageAltitudMaximaChange -> limpiarErrorMessageAltitudMaxima()
            AeronaveEvent.LimpiarErrorMessageLicenciaChange -> limpiarErrorMessageLicencia()
            is AeronaveEvent.AeronaveChange -> aeronaveIdChange(event.aeronaveId)
            is AeronaveEvent.EstadoIdChange -> estadoIdChange(event.estadoId)
            is AeronaveEvent.ModeloAvionChange -> modeloAvionChange(event.modeloAvion)
            is AeronaveEvent.DescripcionCategoriaChange -> descripcionCategoriaChange(event.descripcionCategoria)
            is AeronaveEvent.RegistracionChange -> registracionChange(event.registracion)
            is AeronaveEvent.CostoXHoraChange -> costoXHoraChange(event.costoXHora)
            is AeronaveEvent.DescripcionAeronaveChange -> descripcionAeronaveChange(event.descripcionAeronave)
            is AeronaveEvent.VelocidadMaximaChange -> velocidadMaximaChange(event.velocidadMaxima)
            is AeronaveEvent.DescripcionMotorChange -> descripcionMotorChange(event.descripcionMotor)
            is AeronaveEvent.CapacidadCombustibleChange -> capacidadCombustibleChange(event.capacidadCombustible)
            is AeronaveEvent.ConsumoXHoraChange -> consumoXHoraChange(event.consumoXHora)
            is AeronaveEvent.PesoChange -> pesoChange(event.peso)
            is AeronaveEvent.RangoChange -> rangoChange(event.rango)
            is AeronaveEvent.CapacidadPasajerosChange -> capacidadPasajerosChange(event.capacidadPasajeros)
            is AeronaveEvent.AltitudMaximaChange -> altitudMaximaChange(event.altitudMaxima)
            is AeronaveEvent.LicenciaChange -> licenciaChange(event.licencia)
            AeronaveEvent.New -> nuevo()
            AeronaveEvent.PostAeronave -> addAeronave()
            AeronaveEvent.ResetSuccessMessage -> _uiState.update {
                it.copy(isSuccess = false, successMessage = null)
            }
            is AeronaveEvent.GetAeronave -> findAeronave(event.id)
            AeronaveEvent.Save -> saveAeronave()
            AeronaveEvent.Delete -> deleteAeronave()
            is AeronaveEvent.ImageSelected -> onImageSelected(event.uri)
            is AeronaveEvent.ImageUrlChange -> imageUrlChange(event.url) // Nuevo evento para URL
            is AeronaveEvent.FilterByCategoria -> filterAeronavesByCategoria(event.categoriaId)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private fun filterAeronaves(query: String): List<AeronaveDTO> {
        val baseList = if (_uiState.value.categoriaId != null) {
            _uiState.value.aeronaves.filter { it.estadoId == _uiState.value.categoriaId }
        } else {
            _uiState.value.aeronaves
        }

        return if (query.isBlank()) {
            baseList
        } else {
            baseList.filter {
                it.modeloAvion.contains(query, ignoreCase = true) || it.registracion.contains(query, ignoreCase = true) || it.descripcionAeronave.contains(query, ignoreCase = true)
            }
        }
    }

    fun filterAeronavesByCategoria(categoriaId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(categoriaId = categoriaId) }
            aeronaveRepository.getAeronaves().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        val filteredAeronaves = result.data?.filter { it.estadoId == categoriaId } ?: emptyList()
                        _uiState.update {
                            it.copy(
                                aeronaves = filteredAeronaves,
                                isLoading = false
                            )
                        }
                        _searchResults.value = filterAeronaves(_searchQuery.value)
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = result.message ?: "Error desconocido",
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(imageUri = uri, imageUrl = null) } // Limpiar imageUrl si se selecciona una imagen local
    }

    private fun imageUrlChange(url: String) {
        _uiState.update { it.copy(imageUrl = url, imageUri = null) } // Limpiar imageUri si se ingresa una URL
    }

    private fun saveImage(context: Context, uri: Uri): String? {
        return try {
            val contentResolver = context.contentResolver
            val file = File(context.filesDir, "aeronave_${System.currentTimeMillis()}.jpg")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "Error al guardar la imagen: ${e.message}") }
            null
        }
    }

    private fun limpiarErrorMessageEstadoId() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorEstadoId = null) }
        }
    }

    private fun limpiarErrorMessageModeloAvion() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorModeloAvion = null) }
        }
    }

    private fun limpiarErrorMessageDescripcionCategoria() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorDescripcionCategoria = null) }
        }
    }

    private fun limpiarErrorMessageRegistracion() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorRegistracion = null) }
        }
    }

    private fun limpiarErrorMessageCostoXHora() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorCostoXHora = null) }
        }
    }

    private fun limpiarErrorMessageDescripcionAeronave() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorDescripcionAeronave = null) }
        }
    }

    private fun limpiarErrorMessageVelocidadMaxima() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorVelocidadMaxima = null) }
        }
    }

    private fun limpiarErrorMessageDescripcionMotor() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorDescripcionMotor = null) }
        }
    }

    private fun limpiarErrorMessageCapacidadCombustible() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorCapacidadCombustible = null) }
        }
    }

    private fun limpiarErrorMessageConsumoXHora() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorConsumoXHora = null) }
        }
    }

    private fun limpiarErrorMessagePeso() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorPeso = null) }
        }
    }

    private fun limpiarErrorMessageRango() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorRango = null) }
        }
    }

    private fun limpiarErrorMessageCapacidadPasajeros() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorCapacidadPasajeros = null) }
        }
    }

    private fun limpiarErrorMessageAltitudMaxima() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorAltitudMaxima = null) }
        }
    }

    private fun limpiarErrorMessageLicencia() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorLicencia = null) }
        }
    }

    private fun aeronaveIdChange(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(aeronaveId = id) }
        }
    }

    private fun estadoIdChange(estadoId: Int?) {
        viewModelScope.launch {
            _uiState.update { it.copy(estadoId = estadoId) }
        }
    }

    private fun modeloAvionChange(modelo: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(modeloAvion = modelo) }
        }
    }

    private fun descripcionCategoriaChange(descripcion: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(descripcionCategoria = descripcion) }
        }
    }

    private fun registracionChange(registracion: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(registracion = registracion) }
        }
    }

    private fun costoXHoraChange(costo: Double?) {
        viewModelScope.launch {
            _uiState.update { it.copy(costoXHora = costo) }
        }
    }

    private fun descripcionAeronaveChange(descripcion: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(descripcionAeronave = descripcion) }
        }
    }

    private fun velocidadMaximaChange(velocidad: Double?) {
        viewModelScope.launch {
            _uiState.update { it.copy(velocidadMaxima = velocidad) }
        }
    }

    private fun descripcionMotorChange(descripcion: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(descripcionMotor = descripcion) }
        }
    }

    private fun capacidadCombustibleChange(capacidad: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(capacidadCombustible = capacidad) }
        }
    }

    private fun consumoXHoraChange(consumo: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(consumoXHora = consumo) }
        }
    }

    private fun pesoChange(peso: Double?) {
        viewModelScope.launch {
            _uiState.update { it.copy(peso = peso) }
        }
    }

    private fun rangoChange(rango: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(rango = rango) }
        }
    }

    private fun capacidadPasajerosChange(capacidad: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(capacidadPasajeros = capacidad) }
        }
    }

    private fun altitudMaximaChange(altitud: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(altitudMaxima = altitud) }
        }
    }

    private fun licenciaChange(licencia: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(licencia = licencia) }
        }
    }

    private fun nuevo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    aeronaveId = null,
                    estadoId = 0,
                    modeloAvion = "",
                    descripcionCategoria = "",
                    registracion = "",
                    costoXHora = 0.0,
                    descripcionAeronave = "",
                    velocidadMaxima = 0.0,
                    descripcionMotor = "",
                    capacidadCombustible = 0,
                    consumoXHora = 0,
                    peso = 0.0,
                    rango = 0,
                    capacidadPasajeros = 0,
                    altitudMaxima = 0,
                    licencia = "",
                    imageUri = null,
                    imageUrl = null,
                    errorEstadoId = null,
                    errorModeloAvion = null,
                    errorDescripcionCategoria = null,
                    errorRegistracion = null,
                    errorCostoXHora = null,
                    errorDescripcionAeronave = null,
                    errorVelocidadMaxima = null,
                    errorDescripcionMotor = null,
                    errorCapacidadCombustible = null,
                    errorConsumoXHora = null,
                    errorPeso = null,
                    errorRango = null,
                    errorCapacidadPasajeros = null,
                    errorAltitudMaxima = null,
                    errorLicencia = null,
                    errorMessage = null
                )
            }
        }
    }

    private fun addAeronave() {
        viewModelScope.launch {
            var error = false

            if (_uiState.value.estadoId == null || _uiState.value.estadoId!! <= 0) {
                _uiState.update { it.copy(errorEstadoId = "El ID de estado debe ser mayor que cero *") }
                error = true
            }
            if (_uiState.value.modeloAvion.isBlank()) {
                _uiState.update { it.copy(errorModeloAvion = "El modelo de avión es obligatorio *") }
                error = true
            }
            if (_uiState.value.descripcionCategoria.isBlank()) {
                _uiState.update { it.copy(errorDescripcionCategoria = "La descripción de categoría es obligatoria *") }
                error = true
            }
            if (_uiState.value.registracion.isBlank()) {
                _uiState.update { it.copy(errorRegistracion = "La registración es obligatoria *") }
                error = true
            }
            if (_uiState.value.costoXHora == null || _uiState.value.costoXHora!! <= 0.0) {
                _uiState.update { it.copy(errorCostoXHora = "El costo por hora debe ser mayor que cero *") }
                error = true
            }
            if (_uiState.value.descripcionAeronave.isBlank()) {
                _uiState.update { it.copy(errorDescripcionAeronave = "La descripción de la aeronave es obligatoria *") }
                error = true
            }
            if (_uiState.value.velocidadMaxima == null || _uiState.value.velocidadMaxima!! <= 0.0) {
                _uiState.update { it.copy(errorVelocidadMaxima = "La velocidad máxima debe ser mayor que cero *") }
                error = true
            }
            if (_uiState.value.descripcionMotor.isBlank()) {
                _uiState.update { it.copy(errorDescripcionMotor = "La descripción del motor es obligatoria *") }
                error = true
            }
            if (_uiState.value.capacidadCombustible <= 0) {
                _uiState.update { it.copy(errorCapacidadCombustible = "La capacidad de combustible debe ser mayor que cero *") }
                error = true
            }
            if (_uiState.value.consumoXHora <= 0) {
                _uiState.update { it.copy(errorConsumoXHora = "El consumo por hora debe ser mayor que cero *") }
                error = true
            }
            if (_uiState.value.peso == null || _uiState.value.peso!! <= 0.0) {
                _uiState.update { it.copy(errorPeso = "El peso debe ser mayor que cero *") }
                error = true
            }
            if (_uiState.value.rango <= 0) {
                _uiState.update { it.copy(errorRango = "El rango debe ser mayor que cero *") }
                error = true
            }
            if (_uiState.value.capacidadPasajeros <= 0) {
                _uiState.update { it.copy(errorCapacidadPasajeros = "La capacidad de pasajeros debe ser mayor que cero *") }
                error = true
            }
            if (_uiState.value.altitudMaxima <= 0) {
                _uiState.update { it.copy(errorAltitudMaxima = "La altitud máxima debe ser mayor que cero *") }
                error = true
            }
            if (_uiState.value.licencia.isBlank()) {
                _uiState.update { it.copy(errorLicencia = "La licencia es obligatoria *") }
                error = true
            }
            if (error) return@launch

            try {
                val imagePath = if (_uiState.value.imageUrl != null) {
                    _uiState.value.imageUrl // Usar URL si está presente
                } else {
                    _uiState.value.imageUri?.let { uri -> saveImage(context, uri) }
                }
                aeronaveRepository.saveAeronave(_uiState.value.toEntity(imagePath))
                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        successMessage = "Aeronave guardada correctamente",
                        errorMessage = null,
                        imageUri = null,
                        imageUrl = null
                    )
                }
                getAeronaves()
                nuevo()
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 500) {
                    _uiState.update {
                        it.copy(
                            isSuccess = true,
                            successMessage = "Aeronave guardada. Falló sincronización con el servidor (500).",
                            errorMessage = null
                        )
                    }
                    getAeronaves()
                    nuevo()
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Error en la API: ${e.code()} - ${e.message}",
                            isSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al guardar la aeronave: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    private fun saveAeronave() {
        viewModelScope.launch {
            try {
                val id = _uiState.value.aeronaveId ?: return@launch
                val imagePath = if (_uiState.value.imageUrl != null) {
                    _uiState.value.imageUrl
                } else {
                    _uiState.value.imageUri?.let { uri -> saveImage(context, uri) }
                }
                aeronaveRepository.update(id, _uiState.value.toEntity(imagePath))
                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        successMessage = "Aeronave actualizada correctamente",
                        errorMessage = null,
                        imageUri = null,
                        imageUrl = null
                    )
                }
                getAeronaves()
                nuevo()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al actualizar la aeronave: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    private fun deleteAeronave() {
        viewModelScope.launch {
            try {
                _uiState.value.aeronaveId?.let { id ->
                    _uiState.value.imageUri?.let { uri ->
                        val file = File(uri.path ?: "")
                        if (file.exists()) {
                            file.delete()
                        }
                    }
                    aeronaveRepository.deleteAeronave(id)
                    _uiState.update {
                        it.copy(
                            isSuccess = true,
                            successMessage = "Aeronave eliminada correctamente",
                            errorMessage = null,
                            imageUri = null,
                            imageUrl = null
                        )
                    }
                    getAeronaves()
                    nuevo()
                    delay(2000)
                    _uiEvent.send(UiEvent.NavigateUp)
                } ?: run {
                    _uiState.update {
                        it.copy(
                            errorMessage = "No se seleccionó una aeronave para eliminar",
                            isSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al eliminar la aeronave: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }
        }
    }

    private fun findAeronave(aeronaveId: Int) {
        viewModelScope.launch {
            if (aeronaveId > 0) {
                aeronaveRepository.getAeronave(aeronaveId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val aeronave = resource.data?.firstOrNull()
                            aeronave?.let {
                                _uiState.update {
                                    it.copy(
                                        aeronaveId = aeronave.aeronaveId,
                                        estadoId = aeronave.estadoId,
                                        modeloAvion = aeronave.modeloAvion,
                                        descripcionCategoria = aeronave.descripcionCategoria,
                                        registracion = aeronave.registracion,
                                        costoXHora = aeronave.costoXHora,
                                        descripcionAeronave = aeronave.descripcionAeronave,
                                        velocidadMaxima = aeronave.velocidadMaxima,
                                        descripcionMotor = aeronave.descripcionMotor,
                                        capacidadCombustible = aeronave.capacidadCombustible,
                                        consumoXHora = aeronave.consumoXHora,
                                        peso = aeronave.peso,
                                        rango = aeronave.rango,
                                        capacidadPasajeros = aeronave.capacidadPasajeros,
                                        altitudMaxima = aeronave.altitudMaxima,
                                        licencia = aeronave.licencia,
                                        imageUri = if (aeronave.imagePath?.startsWith("http") == false) {
                                            aeronave.imagePath.let { path -> Uri.fromFile(File(path)) }
                                        } else {
                                            null
                                        },
                                        imageUrl = if (aeronave.imagePath?.startsWith("http") == true) {
                                            aeronave.imagePath
                                        } else {
                                            null
                                        }
                                    )
                                }
                            } ?: run {
                                _uiState.update { it.copy(errorMessage = "Aeronave no encontrada") }
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update { it.copy(errorMessage = resource.message) }
                        }
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
            }
        }
    }

    private fun getAeronaves() {
        viewModelScope.launch {
            aeronaveRepository.getAeronaves().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        val filteredAeronaves = if (_uiState.value.categoriaId != null) {
                            result.data?.filter { it.estadoId == _uiState.value.categoriaId } ?: emptyList()
                        } else {
                            result.data ?: emptyList()
                        }
                        _uiState.update {
                            it.copy(
                                aeronaves = filteredAeronaves,
                                isLoading = false
                            )
                        }
                        _searchResults.value = filterAeronaves(_searchQuery.value)
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = result.message ?: "Error desconocido",
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }
}

fun AeronaveUiState.toEntity(imagePath: String? = null) = AeronaveDTO(
    aeronaveId = aeronaveId,
    estadoId = estadoId ?: 0,
    modeloAvion = modeloAvion,
    descripcionCategoria = descripcionCategoria,
    registracion = registracion,
    costoXHora = costoXHora,
    descripcionAeronave = descripcionAeronave,
    velocidadMaxima = velocidadMaxima,
    descripcionMotor = descripcionMotor,
    capacidadCombustible = capacidadCombustible,
    consumoXHora = consumoXHora,
    peso = peso,
    rango = rango,
    capacidadPasajeros = capacidadPasajeros,
    altitudMaxima = altitudMaxima,
    licencia = licencia,
    imagePath = imagePath ?: imageUrl ?: imageUri?.path
)
