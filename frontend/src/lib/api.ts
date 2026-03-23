const API_BASE = process.env.NEXT_PUBLIC_API_URL || "https://carryon-backend-wb3t.onrender.com";
const ADMIN_KEY = process.env.NEXT_PUBLIC_ADMIN_KEY || "";

export async function apiFetch<T>(
  path: string,
  options?: RequestInit
): Promise<T> {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options?.headers as Record<string, string>),
  };

  // Attach admin key for admin endpoints
  if (path.startsWith("/api/admin") && ADMIN_KEY) {
    headers["x-admin-key"] = ADMIN_KEY;
  }

  const res = await fetch(`${API_BASE}${path}`, {
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
  type: "BIKE" | "CAR" | "VAN" | "TRUCK";
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
