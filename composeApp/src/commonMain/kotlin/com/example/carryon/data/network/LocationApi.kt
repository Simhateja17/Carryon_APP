package com.example.carryon.data.network

import com.example.carryon.data.model.ApiResponse
import com.example.carryon.data.model.AutocompleteResult
import com.example.carryon.data.model.CalculateRouteRequest
import com.example.carryon.data.model.DevicePosition
import com.example.carryon.data.model.GeocodeRequest
import com.example.carryon.data.model.GeocodedPlace
import com.example.carryon.data.model.IsolineRequest
import com.example.carryon.data.model.IsolineResult
import com.example.carryon.data.model.LatLng
import com.example.carryon.data.model.MapConfig
import com.example.carryon.data.model.NearbyPlace
import com.example.carryon.data.model.PlaceResult
import com.example.carryon.data.model.RouteResult
import com.example.carryon.data.model.SnapResult
import com.example.carryon.data.model.SnapToRoadsRequest
import com.example.carryon.data.model.StaticMapResponse
import com.example.carryon.data.model.UpdatePositionRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object LocationApi {
    private val client get() = HttpClientFactory.client

    // ── Existing endpoints (now backed by v2 on the server) ──

    suspend fun searchPlaces(query: String, lat: Double? = null, lng: Double? = null): Result<List<PlaceResult>> = runCatching {
        val response = client.get("/api/location/search-places") {
            parameter("query", query)
            lat?.let { parameter("lat", it) }
            lng?.let { parameter("lng", it) }
        }.body<ApiResponse<List<PlaceResult>>>()
        response.data ?: emptyList()
    }

    suspend fun reverseGeocode(lat: Double, lng: Double): Result<PlaceResult?> = runCatching {
        val response = client.get("/api/location/reverse-geocode") {
            parameter("lat", lat)
            parameter("lng", lng)
        }.body<ApiResponse<PlaceResult>>()
        response.data
    }

    suspend fun calculateRoute(
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double
    ): Result<RouteResult> = runCatching {
        val response = client.post("/api/location/calculate-route") {
            contentType(ContentType.Application.Json)
            setBody(CalculateRouteRequest(originLat, originLng, destLat, destLng))
        }.body<ApiResponse<RouteResult>>()
        response.data ?: RouteResult()
    }

    suspend fun getMapConfig(): Result<MapConfig> = runCatching {
        val response = client.get("/api/location/map-config")
            .body<ApiResponse<MapConfig>>()
        response.data ?: MapConfig()
    }

    suspend fun updatePosition(deviceId: String, latitude: Double, longitude: Double): Result<Unit> = runCatching {
        client.post("/api/location/update-position") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePositionRequest(deviceId, latitude, longitude))
        }
        Unit
    }

    suspend fun getPosition(deviceId: String): Result<DevicePosition> = runCatching {
        val response = client.get("/api/location/get-position/$deviceId")
            .body<ApiResponse<DevicePosition>>()
        response.data ?: DevicePosition()
    }

    // ── New v2 endpoints ──

    suspend fun autocomplete(query: String, lat: Double? = null, lng: Double? = null): Result<List<AutocompleteResult>> = runCatching {
        val response = client.get("/api/location/autocomplete") {
            parameter("query", query)
            lat?.let { parameter("lat", it) }
            lng?.let { parameter("lng", it) }
        }.body<ApiResponse<List<AutocompleteResult>>>()
        response.data ?: emptyList()
    }

    suspend fun searchNearby(lat: Double, lng: Double, categories: String? = null, radius: Int? = null): Result<List<NearbyPlace>> = runCatching {
        val response = client.get("/api/location/nearby") {
            parameter("lat", lat)
            parameter("lng", lng)
            categories?.let { parameter("categories", it) }
            radius?.let { parameter("radius", it) }
        }.body<ApiResponse<List<NearbyPlace>>>()
        response.data ?: emptyList()
    }

    suspend fun geocode(address: String): Result<GeocodedPlace?> = runCatching {
        val response = client.post("/api/location/geocode") {
            contentType(ContentType.Application.Json)
            setBody(GeocodeRequest(address))
        }.body<ApiResponse<GeocodedPlace>>()
        response.data
    }

    suspend fun snapToRoads(points: List<LatLng>): Result<List<LatLng>> = runCatching {
        val response = client.post("/api/location/snap-to-roads") {
            contentType(ContentType.Application.Json)
            setBody(SnapToRoadsRequest(points))
        }.body<ApiResponse<SnapResult>>()
        response.data?.snappedPoints ?: emptyList()
    }

    suspend fun getIsoline(lat: Double, lng: Double, minutes: Int): Result<IsolineResult?> = runCatching {
        val response = client.post("/api/location/isoline") {
            contentType(ContentType.Application.Json)
            setBody(IsolineRequest(lat, lng, minutes))
        }.body<ApiResponse<IsolineResult>>()
        response.data
    }

    suspend fun getStaticMapUrl(lat: Double, lng: Double, zoom: Int = 13, width: Int = 400, height: Int = 300): Result<String> = runCatching {
        val response = client.get("/api/location/static-map") {
            parameter("lat", lat)
            parameter("lng", lng)
            parameter("zoom", zoom)
            parameter("width", width)
            parameter("height", height)
        }.body<ApiResponse<StaticMapResponse>>()
        response.data?.url ?: ""
    }
}
