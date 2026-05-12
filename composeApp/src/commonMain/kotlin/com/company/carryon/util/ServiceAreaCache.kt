package com.company.carryon.util

import com.company.carryon.data.model.ServiceArea
import com.company.carryon.data.network.LocationApi
import kotlinx.datetime.Clock

object ServiceAreaCache {
    private var cachedAreas: List<ServiceArea>? = null
    private var cacheExpiryMs: Long = 0
    private const val CACHE_TTL_MS = 60_000L

    suspend fun getServiceAreas(): List<ServiceArea> {
        val now = Clock.System.now().toEpochMilliseconds()
        cachedAreas?.let { if (now < cacheExpiryMs) return it }

        val areas = LocationApi.getServiceAreas().getOrNull() ?: emptyList()
        cachedAreas = areas
        cacheExpiryMs = now + CACHE_TTL_MS
        return areas
    }

    suspend fun isInServiceArea(lat: Double, lng: Double): Boolean {
        return GeoUtils.isInServiceArea(lat, lng, getServiceAreas())
    }

    fun clearCache() {
        cachedAreas = null
        cacheExpiryMs = 0
    }
}
