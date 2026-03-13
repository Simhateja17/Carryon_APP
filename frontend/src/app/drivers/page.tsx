"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { getAdminDrivers, type DriverListItem } from "@/lib/api";

const statusStyles: Record<string, string> = {
  PENDING: "bg-yellow-100 text-yellow-700",
  IN_REVIEW: "bg-blue-100 text-blue-700",
  APPROVED: "bg-green-100 text-green-700",
  REJECTED: "bg-red-100 text-red-700",
};

const statusLabels: Record<string, string> = {
  PENDING: "Pending",
  IN_REVIEW: "In Review",
  APPROVED: "Approved",
  REJECTED: "Rejected",
};

export default function DriversPage() {
  const [drivers, setDrivers] = useState<DriverListItem[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const res = await getAdminDrivers();
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
              <th className="text-left px-5 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">Verification</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">Documents</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">Vehicle</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">Rating</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">Trips</th>
              <th className="text-left px-5 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider"></th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {drivers.length === 0 ? (
              <tr>
                <td colSpan={8} className="px-5 py-8 text-center text-gray-400">
                  No drivers found
                </td>
              </tr>
            ) : (
              drivers.map((driver) => (
                <tr key={driver.id} className="hover:bg-gray-50">
                  <td className="px-5 py-3">
                    <div>
                      <p className="text-sm font-medium text-gray-900">
                        {driver.name || <span className="text-gray-400 italic">No name</span>}
                      </p>
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
                  <td className="px-5 py-3">
                    <span className={`inline-block px-2 py-0.5 text-xs font-medium rounded-full ${
                      statusStyles[driver.verificationStatus] || "bg-gray-100 text-gray-500"
                    }`}>
                      {statusLabels[driver.verificationStatus] || driver.verificationStatus}
                    </span>
                  </td>
                  <td className="px-5 py-3 text-sm text-gray-700">
                    {driver.documentsCount > 0 ? (
                      <span>
                        {driver.documentsApproved}/{driver.documentsCount}
                        <span className="text-gray-400 text-xs ml-1">approved</span>
                      </span>
                    ) : (
                      <span className="text-gray-400">None</span>
                    )}
                  </td>
                  <td className="px-5 py-3 text-sm text-gray-700">
                    {driver.vehicleSummary ? (
                      <span className="text-xs">{driver.vehicleSummary}</span>
                    ) : (
                      <span className="text-gray-400">None</span>
                    )}
                  </td>
                  <td className="px-5 py-3 text-sm text-gray-700">
                    {driver.rating > 0 ? driver.rating.toFixed(1) : "-"}
                  </td>
                  <td className="px-5 py-3 text-sm text-gray-700">
                    {driver.totalTrips}
                  </td>
                  <td className="px-5 py-3">
                    <Link
                      href={`/drivers/${driver.id}`}
                      className="text-indigo-600 hover:text-indigo-800 text-sm font-medium"
                    >
                      Review
                    </Link>
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
