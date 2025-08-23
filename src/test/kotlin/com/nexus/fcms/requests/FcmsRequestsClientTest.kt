@file:Suppress("MaxLineLength")
package com.nexus.fcms.requests

import kotlinx.coroutines.runBlocking
import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.requests.FcmsRequestsClient
import ly.neptune.nexus.fcms.requests.FcmsRequestsClients
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class FcmsRequestsClientTest {

    private lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    private fun client(enableRetries: Boolean = false): FcmsRequestsClient {
        val cfg = FcmsConfig(
            baseUrl = server.url("").toString().trimEnd('/'),
            tokenProvider = { "test-token" },
            enableRetries = enableRetries,
            maxRetries = 2,
        )
        return FcmsRequestsClients.create(cfg)
    }

    @Test
    fun listPendingRequests_ok_withLinksObject(): Unit = runBlocking {
        val body = """
            {
              "data": [
                {
                  "uuid": "rq-1",
                  "type": {"code": 1, "name": "bank_account_match"},
                  "state": {"code": "pending", "name": "Pending"},
                  "created_at": "2025-08-22T12:00:00Z",
                  "bankAccount": {
                    "uuid": "acc-1",
                    "iban": "LY0001",
                    "state": {"code": "pending", "name": "Pending"},
                    "created_at": "2025-08-22T11:59:59Z",
                    "user": {"id": 11, "first_name": "John", "nid": "1", "passport_number": null, "phone": null}
                  }
                }
              ],
              "links": {"next": null, "prev": null},
              "meta": {"total": 1, "per_page": 15, "current_page": 1}
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        val page = client().listPendingRequests(page = 1)
        assertEquals(1, page.data.size)
        assertEquals("rq-1", page.data[0].uuid)
        assertEquals(1, page.data[0].type?.code)
        assertNull(page.next)
        assertNull(page.prev)
        assertEquals(1, page.total)
    }

    @Test
    fun listPendingRequests_ok_withPageAndHeaders(): Unit = runBlocking {
        val body = """
            {"data": [], "links": {"next": null, "prev": null}, "meta": {"total": 0, "per_page": 15, "current_page": 2}}
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        val c = client()
        val opts = RequestOptions(tokenOverride = "override-token")
        val page = c.listPendingRequests(page = 2, options = opts)
        assertEquals(0, page.data.size)
        assertEquals(2, page.currentPage)

        val recorded = server.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals("/api/v1/purchase-requests-queue?page=2", recorded.path)
        assertEquals("GET", recorded.method)
        assertTrue(recorded.getHeader("Authorization")!!.contains("override-token"))
    }
}
