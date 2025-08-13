package edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo


enum class TipoLicencia(val descripcion: String) {
    PPL("PPL (Licencia de Piloto Privado)"),
    CPL("CPL (Licencia de Piloto Comercial) + Habilitación de Tipo"),
    ATPL("ATPL (Licencia de Transporte de Línea Aérea)"),
    IR("IR (Habilitación de Vuelo por Instrumentos)"),
    ME("Habilitación Multimotor"),
    TURBOPROP("Habilitación Turboprop"),
    JET("Jet Type Rating");

    override fun toString(): String = descripcion
}


fun tipoLicenciaFromDescripcion(descripcion: String): TipoLicencia? {
    return TipoLicencia.entries.firstOrNull {
        it.descripcion.equals(descripcion.trim(), ignoreCase = true)
    }
}

