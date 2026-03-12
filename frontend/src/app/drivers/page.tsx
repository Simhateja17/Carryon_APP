"use client";

import { useEffect, useState } from "react";
import { getDrivers, type Driver } from "@/lib/api";

export default function DriversPage() {
  const [drivers, setDrivers] = useState<Driver[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const res = await getDrivers();
        setDrivers(res.data);
      } catch (err) {
        console.error("Failed to load drivers:", err);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-gray-500">Loading drivers...</div>
      </div>
    );
  }

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Drivers</h1>

      <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        <table className="w-full">
          <thead>
            <tr className="bg-gray-50 border-b border-gray-200">
              <th className="text-left px-5 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">Driver</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">Rating</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">Trips</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">Push</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {drivers.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-5 py-8 text-center text-gray-400">
                  No drivers found
                </td>
              </tr>
            ) : (
              drivers.map((driver) => (
                <tr key={driver.id} className="hover:bg-gray-50">
                  <td className="px-5 py-3">
                    <div>
                      <p className="text-sm font-medium text-gray-900">{driver.name}</p>
                      <p className="text-xs text-gray-500">{driver.email}</p>
                    </div>
                  </td>
                  <td className="px-5 py-3">
                    <span className={`inline-flex items-center gap-1.5 px-2 py-0.5 text-xs rounded-full ${
                      driver.isOnline
                        ? "bg-green-100 text-green-700"
                        : "bg-gray-100 text-gray-500"
                    }`}>
                      <span className={`w-1.5 h-1.5 rounded-full ${driver.isOnline ? "bg-green-500" : "bg-gray-400"}`} />
                      {driver.isOnline ? "Online" : "Offline"}
                    </span>
                  </td>
                  <td className="px-5 py-3 text-sm text-gray-700">
                    {driver.rating > 0 ? driver.rating.toFixed(1) : "-"}
                  </td>
                  <td className="px-5 py-3 text-sm text-gray-700">
                    {driver.totalTrips}
                  </td>
                  <td className="px-5 py-3">
                    <span className={`inline-block px-2 py-0.5 text-xs rounded-full ${
                      driver.hasFcmToken
                        ? "bg-green-100 text-green-700"
                        : "bg-yellow-100 text-yellow-700"
                    }`}>
                      {driver.hasFcmToken ? "Enabled" : "No token"}
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
