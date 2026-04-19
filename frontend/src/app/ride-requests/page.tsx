"use client";

import Script from "next/script";
import { useCallback, useEffect, useRef, useState } from "react";
import {
  createRideRequest,
  getAdminDrivers,
  type CreateRideRequestResult,
  type DriverListItem,
  type DriverRef,
} from "@/lib/api";

const VEHICLE_TYPES = ["BIKE", "CAR", "PICKUP", "VAN_7FT", "VAN_9FT", "LORRY_10FT", "LORRY_14FT", "LORRY_17FT"] as const;

type ColorClass = "green" | "red" | "yellow";

const colorMap: Record<ColorClass, { border: string; header: string; row: string }> = {
  green: { border: "border-green-200", header: "bg-green-100 text-green-800", row: "text-green-900" },
  red: { border: "border-red-200", header: "bg-red-100 text-red-800", row: "text-red-900" },
  yellow: { border: "border-yellow-200", header: "bg-yellow-100 text-yellow-800", row: "text-yellow-900" },
};

const GOOGLE_MAPS_API_KEY = process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY || "";

declare global {
  interface Window {
    google?: any;
  }
}

export default function RideRequestsPage() {
  const mapContainerRef = useRef<HTMLDivElement | null>(null);
  const fromInputRef = useRef<HTMLInputElement | null>(null);
  const toInputRef = useRef<HTMLInputElement | null>(null);
  const mapRef = useRef<any>(null);
  const markersRef = useRef<{ from?: any; to?: any }>({});
  const autocompleteRef = useRef<{ from?: any; to?: any }>({});
  const activePinRef = useRef<"from" | "to">("from");

  const [fromAddress, setFromAddress] = useState("");
  const [fromLat, setFromLat] = useState("");
  const [fromLng, setFromLng] = useState("");
  const [toAddress, setToAddress] = useState("");
  const [toLat, setToLat] = useState("");
  const [toLng, setToLng] = useState("");
  const [recipientEmail, setRecipientEmail] = useState("");
  const [price, setPrice] = useState("");
  const [vehicleType, setVehicleType] = useState<(typeof VEHICLE_TYPES)[number]>("CAR");
  const [activePin, setActivePin] = useState<"from" | "to">("from");
  const [mapReady, setMapReady] = useState(false);
  const [driverOptions, setDriverOptions] = useState<DriverListItem[]>([]);
  const [selectedDriverIds, setSelectedDriverIds] = useState<string[]>([]);
  const [loadingDrivers, setLoadingDrivers] = useState(true);

  const [sending, setSending] = useState(false);
  const [error, setError] = useState("");
  const [result, setResult] = useState<CreateRideRequestResult | null>(null);

  const syncMarker = useCallback((point: "from" | "to", lat: number, lng: number) => {
    if (!mapRef.current || !window.google) return;
    const position = { lat, lng };

    if (!markersRef.current[point]) {
      markersRef.current[point] = new window.google.maps.Marker({
        map: mapRef.current,
        position,
        label: point === "from" ? "A" : "B",
      });
    } else {
      markersRef.current[point].setPosition(position);
    }
  }, []);

  const applyPoint = useCallback(
    (point: "from" | "to", payload: { address?: string; lat: number; lng: number }) => {
      if (point === "from") {
        if (payload.address != null) setFromAddress(payload.address);
        setFromLat(payload.lat.toFixed(6));
        setFromLng(payload.lng.toFixed(6));
      } else {
        if (payload.address != null) setToAddress(payload.address);
        setToLat(payload.lat.toFixed(6));
        setToLng(payload.lng.toFixed(6));
      }

      syncMarker(point, payload.lat, payload.lng);
      if (mapRef.current) {
        mapRef.current.panTo({ lat: payload.lat, lng: payload.lng });
      }
    },
    [syncMarker]
  );

  const attachAutocomplete = useCallback(
    (point: "from" | "to", input: HTMLInputElement | null) => {
      if (!input || !window.google || autocompleteRef.current[point]) return;

      const autocomplete = new window.google.maps.places.Autocomplete(input, {
        fields: ["formatted_address", "geometry"],
      });

      autocomplete.addListener("place_changed", () => {
        const place = autocomplete.getPlace();
        const location = place?.geometry?.location;
        if (!location) return;

        applyPoint(point, {
          address: place.formatted_address || input.value,
          lat: location.lat(),
          lng: location.lng(),
        });
      });

      autocompleteRef.current[point] = autocomplete;
    },
    [applyPoint]
  );

  const initMap = useCallback(() => {
    if (!mapContainerRef.current || !window.google || mapRef.current) return;

    mapRef.current = new window.google.maps.Map(mapContainerRef.current, {
      center: { lat: 3.139, lng: 101.6869 },
      zoom: 11,
      streetViewControl: false,
      mapTypeControl: false,
    });

    mapRef.current.addListener("click", (event: any) => {
      const lat = event?.latLng?.lat?.();
      const lng = event?.latLng?.lng?.();
      if (typeof lat !== "number" || typeof lng !== "number") return;
      applyPoint(activePinRef.current, { lat, lng });
    });

    attachAutocomplete("from", fromInputRef.current);
    attachAutocomplete("to", toInputRef.current);
    setMapReady(true);
  }, [applyPoint, attachAutocomplete]);

  useEffect(() => {
    activePinRef.current = activePin;
  }, [activePin]);

  useEffect(() => {
    if (!mapRef.current || !window.google) return;
    const parsedFromLat = Number(fromLat);
    const parsedFromLng = Number(fromLng);
    if (Number.isFinite(parsedFromLat) && Number.isFinite(parsedFromLng)) {
      syncMarker("from", parsedFromLat, parsedFromLng);
    }

    const parsedToLat = Number(toLat);
    const parsedToLng = Number(toLng);
    if (Number.isFinite(parsedToLat) && Number.isFinite(parsedToLng)) {
      syncMarker("to", parsedToLat, parsedToLng);
    }
  }, [fromLat, fromLng, toLat, toLng, syncMarker]);

  useEffect(() => {
    async function loadDrivers() {
      try {
        const res = await getAdminDrivers();
        setDriverOptions(res.data);
      } catch {
        setDriverOptions([]);
      } finally {
        setLoadingDrivers(false);
      }
    }
    loadDrivers();
  }, []);

  function toggleDriverSelection(driverId: string) {
    setSelectedDriverIds((prev) =>
      prev.includes(driverId) ? prev.filter((id) => id !== driverId) : [...prev, driverId]
    );
  }

  const selectedDrivers = driverOptions.filter((d) => selectedDriverIds.includes(d.id));
  const selectedDriversWithoutToken = selectedDrivers.filter((d) => d.hasFcmToken === false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSending(true);
    setError("");
    setResult(null);

    if (selectedDriversWithoutToken.length > 0) {
      const names = selectedDriversWithoutToken.map((d) => d.name || d.email).join(", ");
      const proceed = window.confirm(
        `Selected driver(s) have no FCM token: ${names}. Push won't be delivered, only polling can pick it up. Continue?`
      );
      if (!proceed) {
        setSending(false);
        return;
      }
    }

    try {
      const response = await createRideRequest({
        from: {
          address: fromAddress.trim(),
          latitude: Number(fromLat),
          longitude: Number(fromLng),
        },
        to: {
          address: toAddress.trim(),
          latitude: Number(toLat),
          longitude: Number(toLng),
          contactEmail: recipientEmail.trim(),
        },
        price: Number(price),
        vehicleType,
        driverIds: selectedDriverIds,
      });

      setResult(response.data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to create ride request");
    } finally {
      setSending(false);
    }
  }

  return (
    <div className="max-w-3xl">
      {GOOGLE_MAPS_API_KEY ? (
        <Script
          src={`https://maps.googleapis.com/maps/api/js?key=${GOOGLE_MAPS_API_KEY}&libraries=places`}
          strategy="afterInteractive"
          onLoad={initMap}
        />
      ) : null}

      <h1 className="text-2xl font-bold text-gray-900 mb-6">Create Test Ride Request</h1>

      <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 space-y-6">
        <div className="space-y-3">
          {!GOOGLE_MAPS_API_KEY ? (
            <div className="bg-amber-50 border border-amber-200 rounded-lg p-3 text-sm text-amber-800">
              Add <code className="font-mono">NEXT_PUBLIC_GOOGLE_MAPS_API_KEY</code> to enable map + autocomplete.
            </div>
          ) : null}
          <div className="flex items-center gap-2">
            <button
              type="button"
              onClick={() => setActivePin("from")}
              className={`px-3 py-1.5 rounded-lg text-sm border ${
                activePin === "from" ? "bg-indigo-600 text-white border-indigo-600" : "bg-white text-gray-700 border-gray-300"
              }`}
            >
              Pin From
            </button>
            <button
              type="button"
              onClick={() => setActivePin("to")}
              className={`px-3 py-1.5 rounded-lg text-sm border ${
                activePin === "to" ? "bg-indigo-600 text-white border-indigo-600" : "bg-white text-gray-700 border-gray-300"
              }`}
            >
              Pin To
            </button>
            <span className="text-xs text-gray-500">
              {mapReady ? "Click on map to set selected pin." : "Loading map..."}
            </span>
          </div>
          <div ref={mapContainerRef} className="h-72 w-full rounded-lg border border-gray-200 bg-gray-100" />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="md:col-span-2">
            <label htmlFor="fromAddress" className="block text-sm font-medium text-gray-700 mb-1">
              From Address
            </label>
            <input
              id="fromAddress"
              ref={fromInputRef}
              type="text"
              value={fromAddress}
              onChange={(e) => setFromAddress(e.target.value)}
              placeholder="Pickup location"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-900"
              required
            />
          </div>
          <div>
            <label htmlFor="fromLat" className="block text-sm font-medium text-gray-700 mb-1">
              From Latitude
            </label>
            <input
              id="fromLat"
              type="number"
              step="any"
              value={fromLat}
              onChange={(e) => setFromLat(e.target.value)}
              placeholder="e.g. 3.1390"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-900"
              required
            />
          </div>
          <div>
            <label htmlFor="fromLng" className="block text-sm font-medium text-gray-700 mb-1">
              From Longitude
            </label>
            <input
              id="fromLng"
              type="number"
              step="any"
              value={fromLng}
              onChange={(e) => setFromLng(e.target.value)}
              placeholder="e.g. 101.6869"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-900"
              required
            />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="md:col-span-2">
            <label htmlFor="toAddress" className="block text-sm font-medium text-gray-700 mb-1">
              To Address
            </label>
            <input
              id="toAddress"
              ref={toInputRef}
              type="text"
              value={toAddress}
              onChange={(e) => setToAddress(e.target.value)}
              placeholder="Drop location"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-900"
              required
            />
          </div>
          <div>
            <label htmlFor="toLat" className="block text-sm font-medium text-gray-700 mb-1">
              To Latitude
            </label>
            <input
              id="toLat"
              type="number"
              step="any"
              value={toLat}
              onChange={(e) => setToLat(e.target.value)}
              placeholder="e.g. 3.1579"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-900"
              required
            />
          </div>
          <div>
            <label htmlFor="toLng" className="block text-sm font-medium text-gray-700 mb-1">
              To Longitude
            </label>
            <input
              id="toLng"
              type="number"
              step="any"
              value={toLng}
              onChange={(e) => setToLng(e.target.value)}
              placeholder="e.g. 101.7117"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-900"
              required
            />
          </div>
          <div className="md:col-span-2">
            <label htmlFor="recipientEmail" className="block text-sm font-medium text-gray-700 mb-1">
              Recipient Email (for delivery OTP)
            </label>
            <input
              id="recipientEmail"
              type="email"
              value={recipientEmail}
              onChange={(e) => setRecipientEmail(e.target.value)}
              placeholder="receiver@example.com"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-900"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label htmlFor="price" className="block text-sm font-medium text-gray-700 mb-1">
              Price
            </label>
            <input
              id="price"
              type="number"
              step="0.01"
              min="0"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              placeholder="e.g. 35.00"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-900"
              required
            />
          </div>
          <div>
            <label htmlFor="vehicleType" className="block text-sm font-medium text-gray-700 mb-1">
              Vehicle Type
            </label>
            <select
              id="vehicleType"
              value={vehicleType}
              onChange={(e) => setVehicleType(e.target.value as (typeof VEHICLE_TYPES)[number])}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-900"
            >
              {VEHICLE_TYPES.map((type) => (
                <option key={type} value={type}>
                  {type}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div>
          <div className="flex items-center justify-between mb-1">
            <label className="block text-sm font-medium text-gray-700">
              Target Drivers (optional)
            </label>
            {selectedDriverIds.length > 0 && (
              <button
                type="button"
                onClick={() => setSelectedDriverIds([])}
                className="text-xs text-indigo-600 hover:text-indigo-800"
              >
                Clear
              </button>
            )}
          </div>
          <div className="rounded-lg border border-gray-300 max-h-52 overflow-y-auto">
            {loadingDrivers ? (
              <div className="px-3 py-2 text-sm text-gray-500">Loading drivers...</div>
            ) : driverOptions.length === 0 ? (
              <div className="px-3 py-2 text-sm text-gray-500">No drivers found</div>
            ) : (
              driverOptions.map((driver) => {
                const checked = selectedDriverIds.includes(driver.id);
                return (
                  <label
                    key={driver.id}
                    className="flex items-center gap-3 px-3 py-2 border-b border-gray-100 last:border-0 cursor-pointer hover:bg-gray-50"
                  >
                    <input
                      type="checkbox"
                      checked={checked}
                      onChange={() => toggleDriverSelection(driver.id)}
                    />
                    <div className="text-sm">
                      <div className="text-gray-900">{driver.name || "No name"}</div>
                      <div className="text-gray-500 text-xs">
                        {driver.email} · {driver.isOnline ? "Online" : "Offline"}
                      </div>
                    </div>
                  </label>
                );
              })
            )}
          </div>
          <p className="mt-1 text-xs text-gray-500">
            If none selected, request is sent to nearby online drivers by location + vehicle filter.
          </p>
          {selectedDriversWithoutToken.length > 0 && (
            <p className="mt-2 text-xs text-amber-700 bg-amber-50 border border-amber-200 rounded px-2 py-1">
              Selected driver(s) without FCM token:{" "}
              {selectedDriversWithoutToken.map((d) => d.name || d.email).join(", ")}
            </p>
          )}
        </div>

        <button
          type="submit"
          disabled={sending}
          className="w-full bg-indigo-600 text-white py-2.5 px-4 rounded-lg text-sm font-medium hover:bg-indigo-700 disabled:opacity-50"
        >
          {sending ? "Dispatching..." : "Dispatch Ride Request"}
        </button>

        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4">
            <p className="text-sm text-red-800">{error}</p>
          </div>
        )}

        {result && (
          <div className="space-y-3">
            <div className="bg-green-50 border border-green-200 rounded-lg p-4 text-sm text-green-800">
              Created booking <span className="font-semibold">{result.bookingId}</span> ({result.status})<br />
              Targeted {result.targetedDrivers.length} driver(s) for {result.vehicleType}
              {result.targetingMode === "selected_drivers" ? " (selected list)" : " (nearby filter)"}.
              <br />
              Push delivery: {result.push.delivered} delivered, {result.push.failed} failed
              {result.push.driversWithoutToken > 0 ? `, ${result.push.driversWithoutToken} no token` : ""}.
            </div>

            <DriverList title="Push Delivered" drivers={result.push.deliveredDrivers} colorClass="green" />
            {result.push.failedDrivers.length > 0 && (
              <DriverList title="Push Failed" drivers={result.push.failedDrivers} colorClass="red" />
            )}
            {result.push.noTokenDrivers.length > 0 && (
              <DriverList title="No FCM Token" drivers={result.push.noTokenDrivers} colorClass="yellow" />
            )}
          </div>
        )}
      </form>
    </div>
  );
}

function DriverList({
  title,
  drivers,
  colorClass,
}: {
  title: string;
  drivers: DriverRef[];
  colorClass: ColorClass;
}) {
  const c = colorMap[colorClass];
  return (
    <div className={`border ${c.border} rounded-lg overflow-hidden`}>
      <div className={`px-4 py-2 text-xs font-semibold uppercase tracking-wide ${c.header}`}>
        {title} ({drivers.length})
      </div>
      {drivers.length === 0 ? (
        <div className="px-4 py-3 text-sm text-gray-500">None</div>
      ) : (
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-100 bg-gray-50 text-gray-500 text-xs">
              <th className="px-4 py-2 text-left font-medium">Name</th>
              <th className="px-4 py-2 text-left font-medium">Email</th>
            </tr>
          </thead>
          <tbody>
            {drivers.map((d) => (
              <tr key={d.id} className="border-b border-gray-50 last:border-0">
                <td className={`px-4 py-2 ${c.row}`}>{d.name}</td>
                <td className={`px-4 py-2 ${c.row} opacity-75`}>{d.email}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
