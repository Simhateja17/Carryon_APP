package com.example.carryon.data.network

import com.example.carryon.data.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
private data class TopUpRequest(val amount: Double, val paymentReference: String? = null)

@Serializable
private data class WalletPayRequest(val bookingId: String)

object WalletApi {
    private val client get() = HttpClientFactory.client

    suspend fun getWallet(): Result<ApiResponse<Wallet>> = runCatching {
        client.get("/api/wallet").body()
    }

    suspend fun topUp(amount: Double, paymentReference: String? = null): Result<ApiResponse<WalletTopUpResponse>> = runCatching {
        client.post("/api/wallet/topup") {
            contentType(ContentType.Application.Json)
            setBody(TopUpRequest(amount, paymentReference))
        }.body()
    }

    suspend fun payWithWallet(bookingId: String): Result<ApiResponse<WalletPayResponse>> = runCatching {
        client.post("/api/wallet/pay") {
            contentType(ContentType.Application.Json)
            setBody(WalletPayRequest(bookingId))
        }.body()
    }

    suspend fun getTransactions(page: Int = 1, limit: Int = 20): Result<ApiResponse<TransactionsPage>> = runCatching {
        client.get("/api/wallet/transactions?page=$page&limit=$limit").body()
    }
}

@Serializable
data class TransactionsPage(
    val transactions: List<WalletTransaction> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val limit: Int = 20
)
