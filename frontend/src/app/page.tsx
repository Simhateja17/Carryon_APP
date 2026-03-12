"use client";

import { useEffect, useState } from "react";
import { getStats, getNotifications, type Stats, type Notification } from "@/lib/api";

export default function DashboardPage() {
  const [stats, setStats] = useState<Stats | null>(null);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const [statsRes, notifsRes] = await Promise.all([
          getStats(),
          getNotifications(),
        ]);
        setStats(statsRes.data);
        setNotifications(notifsRes.data);
      } catch (err) {
        console.error("Failed to load dashboard:", err);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-gray-500">Loading dashboard...</div>
      </div>
    );
  }

  const statCards = stats
    ? [
        { label: "Total Drivers", value: stats.totalDrivers, color: "bg-blue-500" },
        { label: "Online Now", value: stats.onlineDrivers, color: "bg-green-500" },
        { label: "Active Bookings", value: stats.activeBookings, color: "bg-orange-500" },
        { label: "Notifications Sent", value: stats.totalNotifications, color: "bg-purple-500" },
      ]
    : [];

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Dashboard</h1>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {statCards.map((card) => (
          <div key={card.label} className="bg-white rounded-xl shadow-sm border border-gray-200 p-5">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-500">{card.label}</p>
                <p className="text-2xl font-bold text-gray-900 mt-1">{card.value}</p>
              </div>
              <div className={`w-10 h-10 ${card.color} rounded-lg opacity-20`} />
            </div>
          </div>
        ))}
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-200">
        <div className="px-5 py-4 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900">Recent Notifications</h2>
        </div>
        <div className="divide-y divide-gray-100">
          {notifications.length === 0 ? (
            <div className="px-5 py-8 text-center text-gray-400">
              No notifications sent yet
            </div>
          ) : (
            notifications.slice(0, 10).map((notif) => (
              <div key={notif.id} className="px-5 py-3 flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-900">{notif.title}</p>
                  <p className="text-sm text-gray-500">{notif.message}</p>
                </div>
                <div className="text-right shrink-0 ml-4">
                  <span className={`inline-block px-2 py-0.5 text-xs rounded-full ${
                    notif.type === "PROMO" ? "bg-purple-100 text-purple-700" :
                    notif.type === "SYSTEM" ? "bg-blue-100 text-blue-700" :
                    notif.type === "ALERT" ? "bg-red-100 text-red-700" :
                    "bg-gray-100 text-gray-700"
                  }`}>
                    {notif.type}
                  </span>
                  <p className="text-xs text-gray-400 mt-1">
                    {notif.driver?.name || "Unknown"}
                  </p>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
