"use client";

import { useState } from "react";
import { sendNotification, type SendNotificationResult, type DriverRef } from "@/lib/api";

const NOTIFICATION_TYPES = [
  { value: "PROMO", label: "Promotion" },
  { value: "SYSTEM", label: "System" },
  { value: "ALERT", label: "Alert" },
  { value: "PAYMENT", label: "Payment" },
];

const AUDIENCES = [
  { value: "all", label: "All Drivers" },
  { value: "online", label: "Online Drivers Only" },
];

export default function SendNotificationPage() {
  const [title, setTitle] = useState("");
  const [message, setMessage] = useState("");
  const [type, setType] = useState("PROMO");
  const [audience, setAudience] = useState<"all" | "online">("all");
  const [sending, setSending] = useState(false);
  const [result, setResult] = useState<SendNotificationResult | null>(null);
  const [error, setError] = useState("");

  async function handleSend(e: React.FormEvent) {
    e.preventDefault();
    if (!title.trim() || !message.trim()) return;

    setSending(true);
    setResult(null);
    setError("");

    try {
      const res = await sendNotification({ title, message, type, audience });
      setResult(res.data);
      setTitle("");
      setMessage("");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to send notification");
    } finally {
      setSending(false);
    }
  }

  return (
    <div className="max-w-2xl">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Send Push Notification</h1>

      <form onSubmit={handleSend} className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 space-y-5">
        <div>
          <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-1">
            Title
          </label>
          <input
            id="title"
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="e.g. Weekend Bonus!"
            className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-gray-900"
            required
          />
        </div>

        <div>
          <label htmlFor="message" className="block text-sm font-medium text-gray-700 mb-1">
            Message
          </label>
          <textarea
            id="message"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="e.g. Complete 10 deliveries this weekend and earn an extra RM50!"
            rows={3}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-gray-900"
            required
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label htmlFor="type" className="block text-sm font-medium text-gray-700 mb-1">
              Type
            </label>
            <select
              id="type"
              value={type}
              onChange={(e) => setType(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-gray-900"
            >
              {NOTIFICATION_TYPES.map((t) => (
                <option key={t.value} value={t.value}>
                  {t.label}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label htmlFor="audience" className="block text-sm font-medium text-gray-700 mb-1">
              Audience
            </label>
            <select
              id="audience"
              value={audience}
              onChange={(e) => setAudience(e.target.value as "all" | "online")}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-gray-900"
            >
              {AUDIENCES.map((a) => (
                <option key={a.value} value={a.value}>
                  {a.label}
                </option>
              ))}
            </select>
          </div>
        </div>

        <button
          type="submit"
          disabled={sending || !title.trim() || !message.trim()}
          className="w-full bg-indigo-600 text-white py-2.5 px-4 rounded-lg text-sm font-medium hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          {sending ? "Sending..." : "Send Notification"}
        </button>

        {result && (
          <div className="space-y-3">
            <div className="bg-green-50 border border-green-200 rounded-lg p-4">
              <p className="text-sm text-green-800 font-medium">
                Notification saved for {result.driversCount} driver{result.driversCount !== 1 ? "s" : ""}
              </p>
              {result.push && (
                <p className="text-sm text-green-700 mt-1">
                  Push: {result.push.delivered} delivered, {result.push.failed} failed
                  {result.push.driversWithoutToken > 0 && `, ${result.push.driversWithoutToken} no token`}
                </p>
              )}
            </div>

            {result.push && result.push.deliveredDrivers.length > 0 && (
              <DriverList
                title="Delivered"
                drivers={result.push.deliveredDrivers}
                colorClass="green"
              />
            )}

            {result.push && result.push.failedDrivers.length > 0 && (
              <DriverList
                title="Push failed"
                drivers={result.push.failedDrivers}
                colorClass="red"
              />
            )}

            {result.push && result.push.noTokenDrivers.length > 0 && (
              <DriverList
                title="No FCM token (notification saved, no push)"
                drivers={result.push.noTokenDrivers}
                colorClass="yellow"
              />
            )}
          </div>
        )}

        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4">
            <p className="text-sm text-red-800">{error}</p>
          </div>
        )}
      </form>
    </div>
  );
}

type ColorClass = "green" | "red" | "yellow";

const colorMap: Record<ColorClass, { border: string; header: string; row: string; title: string }> = {
  green:  { border: "border-green-200",  header: "bg-green-100 text-green-800",  row: "text-green-900",  title: "text-green-800"  },
  red:    { border: "border-red-200",    header: "bg-red-100 text-red-800",      row: "text-red-900",    title: "text-red-800"    },
  yellow: { border: "border-yellow-200", header: "bg-yellow-100 text-yellow-800",row: "text-yellow-900", title: "text-yellow-800" },
};

function DriverList({ title, drivers, colorClass }: { title: string; drivers: DriverRef[]; colorClass: ColorClass }) {
  const c = colorMap[colorClass];
  return (
    <div className={`border ${c.border} rounded-lg overflow-hidden`}>
      <div className={`px-4 py-2 text-xs font-semibold uppercase tracking-wide ${c.header}`}>
        {title} ({drivers.length})
      </div>
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
    </div>
  );
}
