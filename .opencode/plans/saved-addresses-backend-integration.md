# Plan: Wire SavedAddresses to Backend API

## Goal
When user picks a location on the map in the "Add New Address" dialog, the address is reverse-geocoded and filled in. User adds contact info and saves -- the address gets persisted to the database via the backend API.

## Current State
- `SavedAddressesScreen.kt` has full UI: map picker, reverse geocode, form fields
- Backend `address.routes.js` has CRUD at `/api/addresses` (GET, POST, PUT, DELETE)
- Prisma `Address` model exists with all fields
- **Missing**: No `AddressApi.kt` client, screen uses local `mutableStateListOf` only

## Changes Required

### 1. Create `AddressApi.kt`
**File**: `composeApp/src/commonMain/kotlin/com/example/carryon/data/network/AddressApi.kt`

```kotlin
package com.example.carryon.data.network

import com.example.carryon.data.model.Address
import com.example.carryon.data.model.ApiResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
private data class CreateAddressRequest(
    val label: String,
    val address: String,
    val landmark: String,
    val latitude: Double,
    val longitude: Double,
    val contactName: String,
    val contactPhone: String,
    val type: String
)

object AddressApi {
    private val client get() = HttpClientFactory.client

    suspend fun getAddresses(): Result<List<Address>> = runCatching {
        val response = client.get("/api/addresses")
            .body<ApiResponse<List<Address>>>()
        response.data ?: emptyList()
    }

    suspend fun createAddress(address: Address): Result<Address> = runCatching {
        val response = client.post("/api/addresses") {
            contentType(ContentType.Application.Json)
            setBody(CreateAddressRequest(
                label = address.label,
                address = address.address,
                landmark = address.landmark,
                latitude = address.latitude,
                longitude = address.longitude,
                contactName = address.contactName,
                contactPhone = address.contactPhone,
                type = address.type.name
            ))
        }.body<ApiResponse<Address>>()
        response.data ?: throw Exception("Failed to create address")
    }

    suspend fun updateAddress(id: String, address: Address): Result<Address> = runCatching {
        val response = client.put("/api/addresses/$id") {
            contentType(ContentType.Application.Json)
            setBody(CreateAddressRequest(
                label = address.label,
                address = address.address,
                landmark = address.landmark,
                latitude = address.latitude,
                longitude = address.longitude,
                contactName = address.contactName,
                contactPhone = address.contactPhone,
                type = address.type.name
            ))
        }.body<ApiResponse<Address>>()
        response.data ?: throw Exception("Failed to update address")
    }

    suspend fun deleteAddress(id: String): Result<Unit> = runCatching {
        client.delete("/api/addresses/$id")
        Unit
    }
}
```

### 2. Update `SavedAddressesScreen.kt`

#### 2a. Add imports
```kotlin
import com.example.carryon.data.network.AddressApi
```

#### 2b. Replace local state with API-backed state
Replace:
```kotlin
// TODO: Load saved addresses from API
val addresses = remember { mutableStateListOf<Address>() }
```
With:
```kotlin
val addresses = remember { mutableStateListOf<Address>() }
var isLoading by remember { mutableStateOf(true) }
var errorMessage by remember { mutableStateOf<String?>(null) }
val scope = rememberCoroutineScope()

// Load saved addresses from API
LaunchedEffect(Unit) {
    AddressApi.getAddresses()
        .onSuccess { list ->
            addresses.clear()
            addresses.addAll(list)
        }
        .onFailure { errorMessage = it.message }
    isLoading = false
}
```

#### 2c. Update onSave callback to call API
Replace:
```kotlin
onSave = { newAddress ->
    addresses.add(newAddress)
    showAddDialog = false
}
```
With:
```kotlin
onSave = { newAddress ->
    scope.launch {
        AddressApi.createAddress(newAddress)
            .onSuccess { created ->
                addresses.add(created)
            }
            .onFailure { errorMessage = it.message }
    }
    showAddDialog = false
}
```

#### 2d. Update delete to call API
Replace:
```kotlin
onClick = {
    addresses.removeAll { it.id == addressId }
    showDeleteDialog = null
}
```
With:
```kotlin
onClick = {
    scope.launch {
        AddressApi.deleteAddress(addressId)
            .onSuccess {
                addresses.removeAll { it.id == addressId }
            }
            .onFailure { errorMessage = it.message }
    }
    showDeleteDialog = null
}
```

#### 2e. Add loading indicator
Show a `CircularProgressIndicator` when `isLoading` is true (before showing empty state or list).

#### 2f. Add error snackbar
Show a `Snackbar` when `errorMessage` is not null to inform the user of failures.

## No Backend Changes Needed
The backend already has the complete CRUD API at `/api/addresses` with authentication. No changes are required on the server side.
