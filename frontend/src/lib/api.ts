const API_BASE = process.env.NEXT_PUBLIC_API_URL || "http://localhost:3000";

export async function apiFetch<T>(
  path: string,
  options?: RequestInit
): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...options?.headers,
    },
    ...options,
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
  totalTrips: number;
  rating: number;
  hasFcmToken: boolean;
  createdAt: string;
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

export async function sendNotification(payload: SendNotificationPayload) {
  return apiFetch<{
    success: boolean;
    data: { sent: number; audience: string; driversCount: number };
  }>("/api/admin/notifications/send", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}
