@file:Suppress("MaxLineLength")
package com.nexus.fcms.accounts

import kotlinx.coroutines.runBlocking
import ly.neptune.nexus.fcms.accounts.AccountsListFilter
import ly.neptune.nexus.fcms.accounts.FcmsAccountsClient
import ly.neptune.nexus.fcms.accounts.FcmsAccountsClients
import ly.neptune.nexus.fcms.accounts.model.request.MatchBankAccountRequest
import ly.neptune.nexus.fcms.accounts.model.request.UpdateBankAccountRequest
import ly.neptune.nexus.fcms.core.FcmsConfig
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class FcmsAccountsClientTest {

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

    private fun client(): FcmsAccountsClient {
        val cfg = FcmsConfig(
            baseUrl = server.url("").toString().trimEnd('/'),
            tokenProvider = { "test-token" },
        )
        return FcmsAccountsClients.create(cfg)
    }

    @Test
    fun listAccounts_ok_withFilters_and_pagination(): Unit = runBlocking {
        val body = """
            {
              "data": [
                {
                  "uuid": "a1",
                  "iban": "LY0001",
                  "account_number": "0000000001",
                  "currency": {"code":"LYD","name":"Libyan Dinar"},
                  "state": {"code":"matched","name":"مطابق"},
                  "created_at": "2024-01-15T00:00:00Z",
                  "user": {"id": 2, "first_name": "A", "nid": "1", "passport_number": "P", "phone": "091"}
                }
              ],
              "links": {"next": null, "prev": null},
              "meta": {"total": 1, "per_page": 15, "current_page": 2}
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        val filter = AccountsListFilter(
            state = "pending",
            iban = "LY0001",
            createdOn = "2024-01-15",
            approvedOn = "2024-01-16",
            rejectedOn = "2024-01-17",
            unrejectedOn = "2024-01-18",
            accountNumber = "0000000001",
            hasAccountNumber = true,
        )
        val page = client().listAccounts(page = 2, filter = filter)
        assertEquals(1, page.data.size)
        assertEquals("a1", page.data[0].uuid)
        assertEquals(2, page.currentPage)

        val recorded = server.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals("GET", recorded.method)
        assertTrue(recorded.path!!.startsWith("/api/v1/bank-accounts"))
        assertTrue(recorded.path!!.contains("page=2"))
        assertTrue(recorded.path!!.contains("filter[state]=pending"))
        assertTrue(recorded.path!!.contains("filter[iban]=LY0001"))
        assertTrue(recorded.path!!.contains("filter[created_on]=2024-01-15"))
        assertTrue(recorded.path!!.contains("filter[approved_on]=2024-01-16"))
        assertTrue(recorded.path!!.contains("filter[rejected_on]=2024-01-17"))
        assertTrue(recorded.path!!.contains("filter[unrejected_on]=2024-01-18"))
        assertTrue(recorded.path!!.contains("filter[account_number]=0000000001"))
        assertTrue(recorded.path!!.contains("filter[has_account_number]=true"))
    }

    @Test
    fun matchAccount_ok_and_body(): Unit = runBlocking {
        val resp = """
            {"data": {"uuid":"m1","iban":"LY","account_number":"1",
              "currency":{"code":"LYD","name":"Libyan Dinar"},
              "state":{"code":"matched","name":"مطابق"},
              "created_at":"2024-01-15T00:00:00Z",
              "user":{"id":1,"first_name":"A"}
            }}
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(resp))

        val c = client()
        val model = c.matchAccount("u1", MatchBankAccountRequest(nid = "1", phone = "091", passportNumber = "P", accountNumber = "1", currencyCode = "LYD"))
        assertEquals("m1", model.uuid)

        val recorded = server.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals("/api/v1/bank-accounts/u1/match", recorded.path)
        assertEquals("PATCH", recorded.method)
        assertEquals("application/json; charset=utf-8", recorded.getHeader("Content-Type"))
        val body = recorded.body.readUtf8()
        assertTrue(body.contains("\"nid\""))
        assertTrue(body.contains("\"phone\""))
        assertTrue(body.contains("\"passport_number\""))
        assertTrue(body.contains("\"account_number\""))
        assertTrue(body.contains("\"currency_code\""))
    }

    @Test
    fun rejectAccount_ok_and_body(): Unit = runBlocking {
        val resp = """
            {"data": {"uuid":"r1","iban":null,"account_number":null,
              "currency":null,
              "state":{"code":"rejected","name":"مرفوض"},
              "created_at":"2024-01-15T00:00:00Z",
              "user":null
            }}
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(resp))

        val c = client()
        val model = c.rejectAccount("u2", rejectReason = "invalid_account", rejectReasonNote = "note")
        assertEquals("r1", model.uuid)

        val recorded = server.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals("/api/v1/bank-accounts/u2/reject", recorded.path)
        assertEquals("PATCH", recorded.method)
        val body = recorded.body.readUtf8()
        assertTrue(body.contains("reject_reason"))
        assertTrue(body.contains("reject_reason_note"))
    }

    @Test
    fun unrejectAccount_ok_body(): Unit = runBlocking {
        val resp = """
            {"data": {"uuid":"u3","iban":null,"account_number":null,
              "currency":null,
              "state":{"code":"pending","name":"في الانتظار"},
              "created_at":"2024-01-15T00:00:00Z",
              "user":null
            }}
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(resp))

        val c = client()
        val model = c.unrejectAccount("u3")
        assertEquals("u3", model.uuid)

        val recorded = server.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals("/api/v1/bank-accounts/u3/unreject", recorded.path)
        assertEquals("PATCH", recorded.method)
        assertEquals("{}", recorded.body.readUtf8())
    }

    @Test
    fun updateAccount_ok_and_body(): Unit = runBlocking {
        val resp = """
            {"data": {"uuid":"u4","iban":"LY","account_number":"9",
              "currency":{"code":"USD","name":"US Dollar"},
              "state":{"code":"matched","name":"مطابق"},
              "created_at":"2024-01-15T00:00:00Z",
              "user":null
            }}
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(resp))

        val c = client()
        val model = c.updateAccount("u4", UpdateBankAccountRequest(accountNumber = "9", currencyCode = "USD"))
        assertEquals("u4", model.uuid)

        val recorded = server.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals("/api/v1/bank-accounts/u4/update", recorded.path)
        assertEquals("PATCH", recorded.method)
        val body = recorded.body.readUtf8()
        assertTrue(body.contains("account_number"))
        assertTrue(body.contains("currency_code"))
    }
}
