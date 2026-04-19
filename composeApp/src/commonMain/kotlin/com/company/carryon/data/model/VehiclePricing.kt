package com.company.carryon.data.model

object VehiclePricing {

    data class PriceRates(
        val regular: Double,
        val priority: Double,
        val pooling: Double
    )

    val rates: Map<String, PriceRates> = mapOf(
        "2 Wheeler"          to PriceRates(regular = 0.90,  priority = 1.50,  pooling = 0.68),
        "Car"                to PriceRates(regular = 1.17,  priority = 1.88,  pooling = 0.88),
        "4x4 Pickup"         to PriceRates(regular = 3.40,  priority = 5.90,  pooling = 3.00),
        "Van 7ft"            to PriceRates(regular = 5.40,  priority = 9.44,  pooling = 4.85),
        "Van 9ft"            to PriceRates(regular = 6.40,  priority = 10.69, pooling = 5.83),
        "Small Lorry 10ft"   to PriceRates(regular = 8.23,  priority = 14.40, pooling = 7.40),
        "Medium Lorry 14ft"  to PriceRates(regular = 11.60, priority = 22.60, pooling = 10.44),
        "Large Lorry 17ft"   to PriceRates(regular = 15.60, priority = 26.60, pooling = 13.70),
    )

    // Offloading add-on fee per booking (RM)
    const val OFFLOADING_FEE = 30.0
    const val TAX_RATE = 0.05

    fun ratePerKm(vehicleType: String, deliveryMode: String): Double {
        val r = rates[vehicleType] ?: rates["Car"]!!
        return when (deliveryMode) {
            "Priority" -> r.priority
            "Pooling"  -> r.pooling
            else       -> r.regular
        }
    }

    fun calculate(
        vehicleType: String,
        deliveryMode: String,
        distanceKm: Double,
        withOffloading: Boolean = false
    ): Double {
        return calculateBaseFare(vehicleType, deliveryMode, distanceKm) +
            if (withOffloading) OFFLOADING_FEE else 0.0
    }

    fun calculateBaseFare(
        vehicleType: String,
        deliveryMode: String,
        distanceKm: Double
    ): Double = distanceKm * ratePerKm(vehicleType, deliveryMode)
}
