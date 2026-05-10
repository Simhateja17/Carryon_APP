"use client";

import { useEffect, useMemo, useState } from "react";
import {
  getExtraCharges,
  reviewExtraCharge,
  type BookingExtraChargeRecord,
} from "@/lib/api";

type ChargeStatus = "PENDING" | "APPROVED" | "REJECTED" | "ALL";

const statusOptions: ChargeStatus[] = ["PENDING", "APPROVED", "REJECTED", "ALL"];

function formatAddress(value?: { address?: string; label?: string }) {
  return value?.label || value?.address || "Not available";
}

function formatDate(value?: string | null) {
  if (!value) return "Pending";
  return new Date(value).toLocaleString("en-MY", {
    dateStyle: "medium",
    timeStyle: "short",
  });
}

export default function ExtraChargesPage() {
  const [status, setStatus] = useState<ChargeStatus>("PENDING");
  const [charges, setCharges] = useState<BookingExtraChargeRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [reviewingId, setReviewingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function loadCharges(nextStatus = status) {
    setLoading(true);
    setError(null);
    try {
      const response = await getExtraCharges(nextStatus);
      setCharges(response.data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load extra charges");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadCharges(status);
  }, [status]);

  const pendingCount = useMemo(
    () => charges.filter((charge) => charge.status === "PENDING").length,
    [charges]
  );

  async function review(id: string, decision: "APPROVED" | "REJECTED") {
    setReviewingId(id);
    setError(null);
    try {
      await reviewExtraCharge(id, decision);
      await loadCharges(status);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to review extra charge");
    } finally {
      setReviewingId(null);
    }
  }

  return (
    <div>
      <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Extra Charges</h1>
          <p className="mt-1 text-sm text-gray-500">
            Review toll and parking pass-through claims before customers are charged.
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          {statusOptions.map((option) => (
            <button
              key={option}
              type="button"
              onClick={() => setStatus(option)}
              className={`rounded-lg border px-3 py-2 text-sm font-medium ${
                status === option
                  ? "border-indigo-600 bg-indigo-600 text-white"
                  : "border-gray-200 bg-white text-gray-700 hover:bg-gray-50"
              }`}
            >
              {option === "PENDING" ? `PENDING ${pendingCount ? `(${pendingCount})` : ""}` : option}
            </button>
          ))}
        </div>
      </div>

      {error ? (
        <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      ) : null}

      <div className="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
        <div className="grid grid-cols-[1.2fr_1fr_0.8fr_0.7fr_0.9fr] gap-4 border-b border-gray-100 bg-gray-50 px-5 py-3 text-xs font-semibold uppercase tracking-wide text-gray-500">
          <div>Booking</div>
          <div>Driver</div>
          <div>Charge</div>
          <div>Proof</div>
          <div>Review</div>
        </div>

        {loading ? (
          <div className="px-5 py-10 text-center text-sm text-gray-500">Loading extra charges...</div>
        ) : charges.length === 0 ? (
          <div className="px-5 py-10 text-center text-sm text-gray-500">No extra charges found.</div>
        ) : (
          <div className="divide-y divide-gray-100">
            {charges.map((charge) => (
              <ExtraChargeRow
                key={charge.id}
                charge={charge}
                reviewing={reviewingId === charge.id}
                onApprove={() => review(charge.id, "APPROVED")}
                onReject={() => review(charge.id, "REJECTED")}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

function ExtraChargeRow({
  charge,
  reviewing,
  onApprove,
  onReject,
}: {
  charge: BookingExtraChargeRecord;
  reviewing: boolean;
  onApprove: () => void;
  onReject: () => void;
}) {
  const orderId = charge.booking?.orderCode || charge.bookingId.slice(-8).toUpperCase();
  const isPending = charge.status === "PENDING";

  return (
    <div className="grid grid-cols-[1.2fr_1fr_0.8fr_0.7fr_0.9fr] gap-4 px-5 py-4 text-sm">
      <div>
        <div className="font-semibold text-gray-900">#{orderId}</div>
        <div className="mt-1 text-xs text-gray-500">{formatAddress(charge.booking?.pickupAddress)}</div>
        <div className="text-xs text-gray-400">to {formatAddress(charge.booking?.deliveryAddress)}</div>
      </div>
      <div>
        <div className="font-medium text-gray-900">{charge.driver?.name || "Unknown driver"}</div>
        <div className="mt-1 text-xs text-gray-500">{charge.driver?.phone || "No phone"}</div>
        <div className="text-xs text-gray-400">{formatDate(charge.createdAt)}</div>
      </div>
      <div>
        <div className="font-semibold text-gray-900">RM {Number(charge.amount).toFixed(2)}</div>
        <div className="mt-1 text-xs text-gray-500">{charge.type}</div>
        {charge.note ? <div className="mt-1 text-xs text-gray-400">{charge.note}</div> : null}
      </div>
      <div>
        {charge.proofUrl ? (
          <a
            href={charge.proofUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex rounded-lg border border-gray-200 px-3 py-2 text-xs font-medium text-indigo-700 hover:bg-indigo-50"
          >
            Open proof
          </a>
        ) : (
          <span className="text-xs text-gray-400">No proof available</span>
        )}
      </div>
      <div>
        {isPending ? (
          <div className="flex flex-wrap gap-2">
            <button
              type="button"
              onClick={onApprove}
              disabled={reviewing}
              className="rounded-lg bg-emerald-600 px-3 py-2 text-xs font-semibold text-white disabled:opacity-50"
            >
              Approve
            </button>
            <button
              type="button"
              onClick={onReject}
              disabled={reviewing}
              className="rounded-lg bg-gray-900 px-3 py-2 text-xs font-semibold text-white disabled:opacity-50"
            >
              Reject
            </button>
          </div>
        ) : (
          <span
            className={`inline-flex rounded-full px-2.5 py-1 text-xs font-semibold ${
              charge.status === "APPROVED"
                ? "bg-emerald-50 text-emerald-700"
                : "bg-red-50 text-red-700"
            }`}
          >
            {charge.status}
          </span>
        )}
      </div>
    </div>
  );
}
