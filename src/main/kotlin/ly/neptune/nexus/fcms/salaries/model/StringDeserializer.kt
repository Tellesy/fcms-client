package ly.neptune.nexus.fcms.salaries.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

/**
 * Parse String from JSON that handles null values by converting them to empty strings.
 */
class StringDeserializer : JsonDeserializer<String>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): String {
        return p.valueAsString ?: ""
    }
}
