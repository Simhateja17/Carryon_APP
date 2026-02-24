package com.example.carryon.data.network

import com.example.carryon.data.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*

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
}
