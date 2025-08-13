package edu.ucne.skyplanerent.presentation.rutayviajes.formulario

sealed interface FormularioEvent{

    data class FormularioChange(val formularioId: Int): FormularioEvent
    data class NombreChange(val nombre: String): FormularioEvent
    data class ApellidoChange (val apellido: String): FormularioEvent
    data class CorreoChange(val correo: String): FormularioEvent
    data class TelefonoChange(val telefono: String): FormularioEvent
    data class PasaporteChange(val pasaporte: String): FormularioEvent
    data class CiudadResidenciaChange(val ciudad: String): FormularioEvent
    data object Save: FormularioEvent
    data object Delete: FormularioEvent
    data object New: FormularioEvent

}
