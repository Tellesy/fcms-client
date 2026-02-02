@file:Suppress("CyclomaticComplexMethod", "NestedBlockDepth")

package ly.neptune.nexus.fcms.fxhousesclient.core.http

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.InputStream

internal object JsonSupport {
    val mapper: ObjectMapper = ObjectMapper(JsonFactory())
        .registerModule(KotlinModule.Builder().build())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun <T> readSingleEnvelope(stream: InputStream, clazz: Class<T>): T {
        stream.use {
            val root: JsonNode = mapper.readTree(it)
            val data = root.get("data") ?: root
            return mapper.readValue(data.traverse(), clazz)
        }
    }

    fun <T> readListEnvelope(stream: InputStream, itemClazz: Class<T>): PageResult<T> {
        stream.use {
            val root: JsonNode = mapper.readTree(it)
            val dataNode = root.get("data") ?: error("Missing 'data' field in list response")
            val items: List<T> = dataNode.map { node -> mapper.readValue(node.traverse(), itemClazz) }

            var total: Long? = null
            var perPage: Int? = null
            var currentPage: Int? = null
            var next: String? = null
            var prev: String? = null

            val meta = root.get("meta")
            if (meta != null && meta.isObject) {
                total = meta.get("total")?.asLong()
                perPage = meta.get("per_page")?.asInt()
                currentPage = meta.get("current_page")?.asInt()
                val metaLinks = meta.get("links")
                if (metaLinks != null && metaLinks.isArray) {
                    metaLinks.forEach { ln ->
                        val label = ln.get("label")?.asText()
                        val url = ln.get("url")?.asText()
                        if (label.equals("Previous", ignoreCase = true)) prev = url
                        if (label.equals("Next", ignoreCase = true)) next = url
                    }
                }
            }

            val links = root.get("links")
            if (links != null) {
                if (links.isObject) {
                    val nextNode = links.get("next")
                    val prevNode = links.get("prev")
                    if (next == null && nextNode != null && !nextNode.isNull) next = nextNode.asText()
                    if (prev == null && prevNode != null && !prevNode.isNull) prev = prevNode.asText()
                } else if (links.isArray) {
                    links.forEach { ln ->
                        val label = ln.get("label")?.asText()
                        val url = ln.get("url")?.asText()
                        if (label.equals("Previous", ignoreCase = true)) prev = url
                        if (label.equals("Next", ignoreCase = true)) next = url
                    }
                }
            }

            return PageResult(items, total, perPage, currentPage, next, prev)
        }
    }

    data class PageResult<T>(
        val data: List<T>,
        val total: Long?,
        val perPage: Int?,
        val currentPage: Int?,
        val next: String?,
        val prev: String?,
    )
}
