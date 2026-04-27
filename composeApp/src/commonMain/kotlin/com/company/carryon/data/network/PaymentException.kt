package com.company.carryon.data.network

class InsufficientBalanceException(
    message: String,
    val currentBalance: Double,
    val amountDue: Double,
    val shortfall: Double,
    val currency: String
) : Exception(message)
