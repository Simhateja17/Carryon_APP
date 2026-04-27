const PRODUCTION_URL = "https://api.carryon.my";

let _resolvedBase: string | null = null;

async function getApiBase(): Promise<string> {
  if (_resolvedBase) return _resolvedBase;
  if (process.env.NEXT_PUBLIC_API_URL) {
    _resolvedBase = process.env.NEXT_PUBLIC_API_URL;
    return _resolvedBase;
  }
  if (process.env.NODE_ENV === "development") {
    try {
      const res = await fetch("http://localhost:4999", {
        signal: AbortSignal.timeout(2000),
      });
      const { port } = await res.json();
      _resolvedBase = `http://localhost:${port}`;
      return _resolvedBase;
    } catch {}
  }
  _resolvedBase = PRODUCTION_URL;
  return _resolvedBase;
}

export async function apiFetch<T>(
  path: string,
  options?: RequestInit
): Promise<T> {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options?.headers as Record<string, string>),
  };

  const url = path.startsWith("/api/admin")
    ? path
    : `${await getApiBase()}${path}`;

  const res = await fetch(url, {
    ...options,
    headers,
  });

  if (!res.ok) {
    const error = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(error.message || `API error: ${res.status}`);
  }

  return res.json();
}

export interface Stats {
  totalDrivers: number;
  onlineDrivers: number;
  totalBookings: number;
  activeBookings: number;
  totalNotifications: number;
}

export interface Driver {
  id: string;
  name: string;
  email: string;
  phone: string;
  isOnline: boolean;
  isVerified: boolean;
  verificationStatus: "PENDING" | "IN_REVIEW" | "APPROVED" | "REJECTED";
  totalTrips: number;
  rating: number;
  hasFcmToken: boolean;
  createdAt: string;
}

export interface DriverListItem {
  id: string;
  name: string;
  email: string;
  phone: string;
  photo: string | null;
  isOnline: boolean;
  hasFcmToken?: boolean;
  isVerified: boolean;
  verificationStatus: "PENDING" | "IN_REVIEW" | "APPROVED" | "REJECTED";
  rating: number;
  totalTrips: number;
  emergencyContact: string;
  createdAt: string;
  documentsCount: number;
  documentsApproved: number;
  hasVehicle: boolean;
  vehicleSummary: string | null;
}

export interface DriverDocument {
  id: string;
  driverId: string;
  type: "DRIVERS_LICENSE" | "VEHICLE_REGISTRATION" | "INSURANCE" | "PROFILE_PHOTO" | "ID_PROOF";
  imageUrl: string;
  status: "PENDING" | "APPROVED" | "REJECTED";
  rejectionReason: string | null;
  uploadedAt: string;
}

export interface DriverVehicle {
  id: string;
  driverId: string;
  type: "BIKE" | "CAR" | "PICKUP" | "VAN_7FT" | "VAN_9FT" | "LORRY_10FT" | "LORRY_14FT" | "LORRY_17FT";
  make: string;
  model: string;
  year: number;
  licensePlate: string;
  color: string;
  createdAt: string;
}

export interface DriverDetail {
  id: string;
  name: string;
  email: string;
  phone: string;
  photo: string | null;
  rating: number;
  totalTrips: number;
  isOnline: boolean;
  isVerified: boolean;
  verificationStatus: "PENDING" | "IN_REVIEW" | "APPROVED" | "REJECTED";
  emergencyContact: string;
  createdAt: string;
  documents: DriverDocument[];
  vehicle: DriverVehicle | null;
}

export interface Notification {
  id: string;
  driverId: string;
  title: string;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
  driver?: { id: string; name: string; email: string };
}

export interface SendNotificationPayload {
  title: string;
  message: string;
  type: string;
  audience: "all" | "online";
}

export async function getStats() {
  return apiFetch<{ success: boolean; data: Stats }>(
    "/api/admin/notifications/stats"
  );
}

export async function getDrivers() {
  return apiFetch<{ success: boolean; data: Driver[] }>(
    "/api/admin/notifications/drivers"
  );
}

export async function getNotifications(page = 1) {
  return apiFetch<{
    success: boolean;
    data: Notification[];
    total: number;
    page: number;
    limit: number;
  }>(`/api/admin/notifications?page=${page}`);
}

export interface DriverRef {
  id: string;
  name: string;
  email: string;
}

export interface PushResult {
  attempted: number;
  delivered: number;
  failed: number;
  driversWithoutToken: number;
  deliveredDrivers: DriverRef[];
  failedDrivers: DriverRef[];
  noTokenDrivers: DriverRef[];
}

export interface SendNotificationResult {
  sent: number;
  audience: string;
  driversCount: number;
  push?: PushResult;
}

export async function sendNotification(payload: SendNotificationPayload) {
  return apiFetch<{
    success: boolean;
    data: SendNotificationResult;
  }>("/api/admin/notifications/send", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export interface RideLocationPayload {
  address: string;
  latitude: number;
  longitude: number;
  contactName?: string;
  contactPhone?: string;
  contactEmail?: string;
  landmark?: string;
}

export interface CreateRideRequestPayload {
  from: RideLocationPayload;
  to: RideLocationPayload;
  price: number;
  vehicleType: "BIKE" | "CAR" | "PICKUP" | "VAN_7FT" | "VAN_9FT" | "LORRY_10FT" | "LORRY_14FT" | "LORRY_17FT";
  paymentMethod?: "CASH" | "UPI" | "CARD" | "WALLET";
  driverIds?: string[];
}

export interface CreateRideRequestResult {
  bookingId: string;
  status: string;
  vehicleType: string;
  estimatedPrice: number;
  distance: number;
  duration: number;
  targetedDrivers: DriverRef[];
  targetingMode?: "selected_drivers" | "nearby_online_drivers";
  push: PushResult;
}

export async function createRideRequest(payload: CreateRideRequestPayload) {
  return apiFetch<{
    success: boolean;
    data: CreateRideRequestResult;
  }>("/api/admin/notifications/ride-request", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export interface AdminRecipientOtpRecord {
  bookingId: string;
  orderCode: string;
  bookingStatus: string;
  dispatchSource: string;
  recipientName: string;
  recipientEmail: string;
  deliveryOtp: string;
  otpSentAt: string | null;
  otpVerifiedAt: string | null;
  createdAt: string | null;
  driver: { id: string; name: string; email: string } | null;
}

export async function getRecipientOtps(status: "all" | "active" | "verified" = "all", limit = 100) {
  return apiFetch<{
    success: boolean;
    data: AdminRecipientOtpRecord[];
  }>(`/api/admin/notifications/recipient-otps?status=${status}&limit=${limit}`);
}

// ── Admin Driver Management ──────────────────────────────────

export async function getAdminDrivers() {
  return apiFetch<{ success: boolean; data: DriverListItem[] }>(
    "/api/admin/drivers"
  );
}

export async function getDriverDetail(id: string) {
  return apiFetch<{ success: boolean; data: DriverDetail }>(
    `/api/admin/drivers/${id}`
  );
}

export async function reviewDocument(
  driverId: string,
  docId: string,
  status: "APPROVED" | "REJECTED",
  rejectionReason?: string
) {
  return apiFetch<{ success: boolean; data: DriverDocument }>(
    `/api/admin/drivers/${driverId}/documents/${docId}/review`,
    {
      method: "PUT",
      body: JSON.stringify({ status, rejectionReason }),
    }
  );
}

export async function updateDriverVerification(
  driverId: string,
  verificationStatus: "PENDING" | "IN_REVIEW" | "APPROVED" | "REJECTED"
) {
  return apiFetch<{ success: boolean; data: DriverDetail }>(
    `/api/admin/drivers/${driverId}/verify`,
    {
      method: "PUT",
      body: JSON.stringify({ verificationStatus }),
    }
  );
}
