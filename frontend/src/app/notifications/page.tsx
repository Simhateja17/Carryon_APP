"use client";

import { useState } from "react";
import { sendNotification } from "@/lib/api";

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
  const [result, setResult] = useState<{ sent: number; driversCount: number } | null>(null);
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
          <div className="bg-green-50 border border-green-200 rounded-lg p-4">
            <p className="text-sm text-green-800 font-medium">
              Notification sent to {result.driversCount} driver{result.driversCount !== 1 ? "s" : ""}
            </p>
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
