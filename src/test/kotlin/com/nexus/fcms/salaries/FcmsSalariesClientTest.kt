@file:Suppress("MaxLineLength")
package com.nexus.fcms.salaries

import ly.neptune.nexus.fcms.core.http.FcmsHttpException
import ly.neptune.nexus.fcms.core.FcmsConfig
import ly.neptune.nexus.fcms.core.RequestOptions
import ly.neptune.nexus.fcms.salaries.FcmsSalariesClient
import ly.neptune.nexus.fcms.salaries.FcmsSalariesClients
import ly.neptune.nexus.fcms.salaries.model.request.CompleteTransactionRequest
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class FcmsSalariesClientTest {

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

    private fun client(enableRetries: Boolean = false): FcmsSalariesClient {
        val cfg = FcmsConfig(
            baseUrl = server.url("").toString().trimEnd('/'),
            tokenProvider = { "test-token" },
            enableRetries = enableRetries,
            maxRetries = 2,
        )
        return FcmsSalariesClients.create(cfg)
    }

    @Test
    fun listTransactions_ok_withLinksObject(): Unit = runBlocking {
        val body = """
            {
              "data": [
                {
                  "uuid": "u1", "state": "pending",
                  "individual": {"name":"A","nid":"1","mofFinancialNumber":"m1","phoneNumber":null},
                  "bankAccount": {"number":"acc","iban":null},
                  "salary": {"amount":"10.50","currency":"LYD","period": {"year":"2025","month":"8"}}
                }
              ],
              "links": {"next": "http://next", "prev": null},
              "meta": {"total": 1, "per_page": 15, "current_page": 1}
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        val page = client().listTransactions(page = 1)
        assertEquals(1, page.data.size)
        assertEquals("u1", page.data[0].uuid)
        assertEquals("http://next", page.next)
        assertNull(page.prev)
        assertEquals(1, page.total)
        assertEquals(15, page.perPage)
        assertEquals(1, page.currentPage)
    }

    @Test
    fun listTransactions_ok_withLinksArray(): Unit = runBlocking {
        val body = """
            {
              "data": [],
              "links": [
                {"url": "http://prev", "label": "Previous", "active": false},
                {"url": "http://next", "label": "Next", "active": false}
              ],
              "meta": {"total": 0, "per_page": 15, "current_page": 1}
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        val page = client().listTransactions(page = null)
        assertEquals(0, page.data.size)
        assertEquals("http://next", page.next)
        assertEquals("http://prev", page.prev)
    }

    @Test
    fun showTransaction_ok(): Unit = runBlocking {
        val body = """
            {
              "data": {
                "uuid": "abc", "state": "completed",
                "individual": {"name":"A","nid":"1","mofFinancialNumber":"m","phoneNumber":null},
                "bankAccount": {"number":"acc","iban":null},
                "salary": {"amount":"100.001","currency":"LYD","period": {"year":"2025","month":"8"}}
              }
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        val tx = client().showTransaction("abc")
        assertEquals("abc", tx.uuid)
        assertEquals("100.001", tx.salary.amount.toPlainString())
    }

    @Test
    fun completeTransaction_ok_and_body(): Unit = runBlocking {
        val resp = """
            {"data": {"uuid":"x","state":"completed",
              "individual": {"name":"A","nid":"1","mofFinancialNumber":"m","phoneNumber":null},
              "bankAccount": {"number":"acc","iban":null},
              "salary": {"amount":"1.0","currency":"LYD","period": {"year":"2025","month":"8"}}
            }}
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(resp))

        val c = client()
        val tx = c.completeTransaction("id-1", CompleteTransactionRequest("ref","123"))
        assertEquals("x", tx.uuid)

        val recorded = server.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals("/api/v1/mof/transactions/id-1/complete", recorded.path)
        assertEquals("POST", recorded.method)
        assertEquals("application/json; charset=utf-8", recorded.getHeader("Content-Type"))
        assertTrue(recorded.getHeader("Authorization")!!.startsWith("Bearer "))
        assertTrue(recorded.body.readUtf8().contains("bank_transaction_reference"))
    }

    @Test
    fun rejectTransaction_ok_and_body(): Unit = runBlocking {
        val resp = """
            {"data": {"uuid":"y","state":"rejected",
              "individual": {"name":"A","nid":"1","mofFinancialNumber":"m","phoneNumber":null},
              "bankAccount": {"number":"acc","iban":null},
              "salary": {"amount":"1.0","currency":"LYD","period": {"year":"2025","month":"8"}}
            }}
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(resp))

        val c = client()
        val tx = c.rejectTransaction("id-2", "invalid_account")
        assertEquals("y", tx.uuid)

        val recorded = server.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals("/api/v1/mof/transactions/id-2/reject", recorded.path)
        assertEquals("POST", recorded.method)
        assertTrue(recorded.body.readUtf8().contains("rejection_reason"))
    }

    @Test
    fun listRejectionReasons_ok(): Unit = runBlocking {
        val body = """
            {"data": [{"code":"invalid_account","name":"حساب غير صحيح"}]}
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        val reasons = client().listRejectionReasons()
        assertEquals(1, reasons.size)
        assertEquals("invalid_account", reasons.first().code)
    }

    @Test
    fun error_401_maps_exception(): Unit = runBlocking {
        val body = """
            {"message": "Unauthenticated."}
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(401).setBody(body))

        val ex = assertThrows(FcmsHttpException::class.java) {
            runBlocking { client().showTransaction("id") }
        }
        assertEquals(401, ex.status)
        assertEquals("Unauthenticated.", ex.message)
        assertTrue(ex.body!!.contains("Unauthenticated"))
    }

    @Test
    fun retry_429_respected_when_enabled(): Unit = runBlocking {
        server.enqueue(MockResponse().setResponseCode(429).setHeader("Retry-After", "1").setBody("{\"message\":\"rate\"}"))
        server.enqueue(MockResponse().setResponseCode(200).setBody("{\"data\":{\"uuid\":\"z\",\"state\":\"completed\",\"individual\":{\"name\":\"A\",\"nid\":\"1\",\"mofFinancialNumber\":\"m\",\"phoneNumber\":null},\"bankAccount\":{\"number\":\"acc\",\"iban\":null},\"salary\":{\"amount\":\"1\",\"currency\":\"LYD\",\"period\":{\"year\":\"2025\",\"month\":\"8\"}}}}"))

        val tx = client(enableRetries = true).showTransaction("z")
        assertEquals("z", tx.uuid)
    }

    @Test
    fun perCallOverrides_baseUrl_and_token(): Unit = runBlocking {
        val body = """
            {"data": []}
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        val cfg = FcmsConfig(
            baseUrl = "https://unused.example.com",
            tokenProvider = { "default-token" },
        )
        val c = FcmsSalariesClients.create(cfg)
        val opts = RequestOptions(baseUrlOverride = server.url("").toString().trimEnd('/'), tokenOverride = "other-token")
        val page = c.listTransactions(page = null, options = opts)
        assertEquals(0, page.data.size)

        val recorded = server.takeRequest(1, TimeUnit.SECONDS)!!
        assertTrue(recorded.getHeader("Authorization")!!.contains("other-token"))
    }
}
