"use client";

import { useEffect, useMemo, useState } from "react";
import { getRecipientOtps, type AdminRecipientOtpRecord } from "@/lib/api";

const statusOptions = [
  { value: "all", label: "All" },
  { value: "active", label: "Active OTPs" },
  { value: "verified", label: "Verified" },
] as const;

export default function RecipientOtpsPage() {
  const [status, setStatus] = useState<(typeof statusOptions)[number]["value"]>("all");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [records, setRecords] = useState<AdminRecipientOtpRecord[]>([]);

  async function load() {
    setLoading(true);
    setError("");
    try {
      const res = await getRecipientOtps(status, 120);
      setRecords(res.data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load recipient OTPs");
      setRecords([]);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, [status]);

  const activeCount = useMemo(() => records.filter((r) => !!r.deliveryOtp && !r.otpVerifiedAt).length, [records]);
  const verifiedCount = useMemo(() => records.filter((r) => !!r.otpVerifiedAt).length, [records]);

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Recipient OTP Monitor</h1>
          <p className="text-sm text-gray-500 mt-1">
            Admin-dispatched rides only. Use this for QA/testing delivery OTP flows.
          </p>
        </div>
        <button
          onClick={load}
          className="px-3 py-2 rounded-lg text-sm bg-indigo-600 text-white hover:bg-indigo-700"
        >
          Refresh
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard label="Total" value={records.length} />
        <StatCard label="Active OTPs" value={activeCount} />
        <StatCard label="Verified" value={verifiedCount} />
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-4">
        <div className="flex items-center gap-2">
          <label className="text-sm text-gray-700 font-medium">Filter:</label>
          <select
            value={status}
            onChange={(e) => setStatus(e.target.value as (typeof statusOptions)[number]["value"])}
            className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm text-gray-900"
          >
            {statusOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-gray-50 text-gray-600">
              <tr>
                <th className="text-left px-4 py-3">Order</th>
                <th className="text-left px-4 py-3">Recipient</th>
                <th className="text-left px-4 py-3">OTP</th>
                <th className="text-left px-4 py-3">Status</th>
                <th className="text-left px-4 py-3">Driver</th>
                <th className="text-left px-4 py-3">Sent</th>
                <th className="text-left px-4 py-3">Verified</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {loading ? (
                <tr>
                  <td className="px-4 py-8 text-gray-500" colSpan={7}>Loading recipient OTP records...</td>
                </tr>
              ) : error ? (
                <tr>
                  <td className="px-4 py-8 text-red-600" colSpan={7}>{error}</td>
                </tr>
              ) : records.length === 0 ? (
                <tr>
                  <td className="px-4 py-8 text-gray-500" colSpan={7}>No records found.</td>
                </tr>
              ) : (
                records.map((record) => {
                  const isVerified = !!record.otpVerifiedAt;
                  return (
                    <tr key={record.bookingId} className="align-top">
                      <td className="px-4 py-3">
                        <div className="font-semibold text-gray-900">{record.orderCode || record.bookingId.slice(0, 8)}</div>
                        <div className="text-xs text-gray-500">{record.bookingId}</div>
                      </td>
                      <td className="px-4 py-3">
                        <div className="font-medium text-gray-900">{record.recipientName || "--"}</div>
                        <div className="text-xs text-gray-500">{record.recipientEmail || "--"}</div>
                      </td>
                      <td className="px-4 py-3">
                        {record.deliveryOtp ? (
                          <span className="font-mono text-lg tracking-widest text-indigo-700">{record.deliveryOtp}</span>
                        ) : (
                          <span className="text-gray-400">--</span>
                        )}
                      </td>
                      <td className="px-4 py-3">
                        <span
                          className={`inline-flex px-2 py-0.5 rounded-full text-xs font-semibold ${
                            isVerified
                              ? "bg-green-100 text-green-700"
                              : record.deliveryOtp
                              ? "bg-amber-100 text-amber-700"
                              : "bg-gray-100 text-gray-700"
                          }`}
                        >
                          {isVerified ? "VERIFIED" : record.deliveryOtp ? "ACTIVE" : "PENDING"}
                        </span>
                        <div className="text-xs text-gray-500 mt-1">{record.bookingStatus}</div>
                      </td>
                      <td className="px-4 py-3">
                        <div className="text-gray-900">{record.driver?.name || "Unassigned"}</div>
                        <div className="text-xs text-gray-500">{record.driver?.email || "--"}</div>
                      </td>
                      <td className="px-4 py-3 text-gray-700">{formatDateTime(record.otpSentAt)}</td>
                      <td className="px-4 py-3 text-gray-700">{formatDateTime(record.otpVerifiedAt)}</td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

function StatCard({ label, value }: { label: string; value: number }) {
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-4">
      <p className="text-sm text-gray-500">{label}</p>
      <p className="text-2xl font-bold text-gray-900 mt-1">{value}</p>
    </div>
  );
}

function formatDateTime(value: string | null) {
  if (!value) return "--";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "--";
  return date.toLocaleString();
}
