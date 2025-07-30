package edu.ucne.skyplanerent.presentation.pago

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// Para formatear el número de tarjeta (ej: 4242 4242 4242 4242)
// Para formatear la fecha de expiración (ej: 12/25)
class DateFilter : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Tomar solo dígitos y limitar a 4 caracteres
        val digits = text.text.filter { it.isDigit() }.take(4)

        // Construir el formato MM/AA
        val formatted = buildString {
            if (digits.isNotEmpty()) {
                append(digits.take(2))
                if (digits.length > 2) {
                    append("/")
                    append(digits.drop(2))
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Mapeo de posiciones del texto original (solo dígitos) al formateado
                return when {
                    digits.isEmpty() -> 0
                    offset <= 1 -> offset
                    offset <= 3 -> offset + 1
                    else -> formatted.length
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Mapeo inverso: del texto formateado al original
                return when {
                    digits.isEmpty() -> 0
                    offset <= 2 -> offset
                    offset <= 4 -> offset - 1
                    else -> digits.length
                }
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
class CreditCardFilter : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(16)
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i != 15) out += " "
        }
        return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)
    }

    private val creditCardOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return when {
                offset <= 3 -> offset
                offset <= 7 -> offset + 1
                offset <= 11 -> offset + 2
                offset <= 16 -> offset + 3
                else -> 19
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            return when {
                offset <= 4 -> offset
                offset <= 9 -> offset - 1
                offset <= 14 -> offset - 2
                offset <= 19 -> offset - 3
                else -> 16
            }
        }
    }
}