package ly.neptune.nexus.fcms.salaries.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.math.BigDecimal

/**
 * Parse BigDecimal from JSON string numbers reliably.
 */
class StringBigDecimalDeserializer : JsonDeserializer<BigDecimal>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): BigDecimal {
        val node = p.text?.trim()
        if (node.isNullOrEmpty()) return BigDecimal.ZERO
        return try {
            BigDecimal(node)
        } catch (ex: NumberFormatException) {
            throw ctxt.weirdStringException(node, BigDecimal::class.java, "Invalid decimal format")
        }
    }
}
