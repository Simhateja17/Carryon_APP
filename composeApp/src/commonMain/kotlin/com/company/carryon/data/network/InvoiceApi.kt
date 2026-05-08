package com.company.carryon.data.network

import com.company.carryon.data.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object InvoiceApi {
    private val client get() = HttpClientFactory.client

    suspend fun generateInvoice(bookingId: String): Result<ApiResponse<Invoice>> = runCatching {
        client.post("/api/invoices/$bookingId").body()
    }

    suspend fun getInvoice(bookingId: String): Result<ApiResponse<Invoice>> = runCatching {
        client.get("/api/invoices/$bookingId").body()
    }

    suspend fun getInvoices(): Result<ApiResponse<List<Invoice>>> = runCatching {
        client.get("/api/invoices").body()
    }

    suspend fun getInvoiceDetail(bookingId: String): Result<ApiResponse<InvoiceDetail>> = runCatching {
        client.get("/api/invoices/$bookingId/detail").body()
    }

    suspend fun getReceiptDownloadUrl(bookingId: String): Result<ApiResponse<InvoiceDownloadLink>> = runCatching {
        client.get("/api/invoices/$bookingId/download").body()
    }

    suspend fun getMonthlyStatementUrl(month: Int, year: Int): Result<ApiResponse<InvoiceDownloadLink>> = runCatching {
        client.get("/api/invoices/statement/download") {
            url {
                parameters.append("month", month.toString())
                parameters.append("year", year.toString())
            }
        }.body()
    }
}
