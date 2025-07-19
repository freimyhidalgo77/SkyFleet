package edu.ucne.skyplanerent.presentation.ruta_y_viajes

class PreReservaViewModel {



    /*fun savePreReserva() {
        viewModelScope.launch {
            try {
                rutaRepository.saveRuta(_uiState.value.toEntity())
                tipoRutaRepository.saveTipoVuelo(_uiState.value.toEntity())
                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        successMessage = "Ruta guardada correctamente",
                        errorMessage = null
                    )
                }
                getRutas()
                nuevo()
                delay(2000)
                _uiEvent.send(UiEvent.NavigateUp)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al guardar la ruta: ${e.localizedMessage}",
                        isSuccess = false
                    )
                }
            }
        }
    }*/


}