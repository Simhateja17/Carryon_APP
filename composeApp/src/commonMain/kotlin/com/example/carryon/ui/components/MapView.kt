package com.example.carryon.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.carryon.data.model.LatLng

data class MapMarker(
    val id: String,
    val lat: Double,
    val lng: Double,
    val title: String,
    val color: MarkerColor = MarkerColor.RED
)

enum class MarkerColor { RED, BLUE, GREEN }

@Composable
expect fun MapViewComposable(
    modifier: Modifier = Modifier,
    styleUrl: String = "",
    centerLat: Double = 17.385,
    centerLng: Double = 78.4867,
    zoom: Double = 12.0,
    markers: List<MapMarker> = emptyList(),
    routeGeometry: List<LatLng>? = null,
    polygonGeometry: List<LatLng>? = null,
    onMapClick: ((Double, Double) -> Unit)? = null
)
