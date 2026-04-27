"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import {
  getDriverDetail,
  reviewDocument,
  updateDriverVerification,
  type DriverDetail,
  type DriverDocument,
} from "@/lib/api";

const DOC_TYPE_LABELS: Record<string, string> = {
  DRIVERS_LICENSE: "Driver's License",
  DRIVERS_LICENSE_BACK: "Driver's License Back",
  GDL: "GDL",
  VEHICLE_REGISTRATION: "Vehicle Registration",
  ROAD_TAX: "Road Tax",
  PUSPAKOM: "PUSPAKOM",
  APAD_PERMIT: "APAD / LPKP Permit",
  VEHICLE_PHOTO_FRONT: "Vehicle Front Photo",
  VEHICLE_PHOTO_BACK: "Vehicle Back Photo",
  VEHICLE_PHOTO_LEFT: "Vehicle Left Photo",
  VEHICLE_PHOTO_RIGHT: "Vehicle Right Photo",
  VEHICLE_PHOTO_INTERIOR: "Vehicle Interior Photo",
  BANK_STATEMENT: "Bank Statement",
  POLICE_CLEARANCE: "Police Clearance",
  INSURANCE: "Insurance Certificate",
  PROFILE_PHOTO: "Profile Photo",
  ID_PROOF: "Government ID",
  MYKAD_FRONT: "MyKad Front",
  MYKAD_BACK: "MyKad Back",
  SELFIE: "Selfie",
  PASSPORT: "Passport",
  WORK_PERMIT_PLKS: "Work Permit / PLKS",
};

const DOC_STATUS_STYLES: Record<string, string> = {
  PENDING: "bg-yellow-100 text-yellow-700",
  APPROVED: "bg-green-100 text-green-700",
  REJECTED: "bg-red-100 text-red-700",
};

const VERIFICATION_STYLES: Record<string, string> = {
  PENDING: "bg-yellow-100 text-yellow-800 border-yellow-200",
  IN_REVIEW: "bg-blue-100 text-blue-800 border-blue-200",
  APPROVED: "bg-green-100 text-green-800 border-green-200",
  REJECTED: "bg-red-100 text-red-800 border-red-200",
};

const VERIFICATION_LABELS: Record<string, string> = {
  PENDING: "Pending",
  IN_REVIEW: "In Review",
  APPROVED: "Approved",
  REJECTED: "Rejected",
};

export default function DriverDetailPage() {
  const params = useParams();
  const router = useRouter();
  const driverId = params.id as string;

  const [driver, setDriver] = useState<DriverDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionLoading, setActionLoading] = useState<string | null>(null);
  const [rejectDocId, setRejectDocId] = useState<string | null>(null);
  const [rejectionReason, setRejectionReason] = useState("");
  const [previewImage, setPreviewImage] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  async function loadDriver() {
    try {
      setLoading(true);
      const res = await getDriverDetail(driverId);
      setDriver(res.data);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load driver");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadDriver();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [driverId]);

  async function handleApproveDoc(doc: DriverDocument) {
    setActionLoading(doc.id);
    setActionError(null);
    try {
      await reviewDocument(driverId, doc.id, "APPROVED");
      await loadDriver();
    } catch (err) {
      setActionError(err instanceof Error ? err.message : "Failed to approve document");
    } finally {
      setActionLoading(null);
    }
  }

  async function handleRejectDoc() {
    if (!rejectDocId || !rejectionReason.trim()) return;
    setActionLoading(rejectDocId);
    setActionError(null);
    try {
      await reviewDocument(driverId, rejectDocId, "REJECTED", rejectionReason.trim());
      setRejectDocId(null);
      setRejectionReason("");
      await loadDriver();
    } catch (err) {
      setActionError(err instanceof Error ? err.message : "Failed to reject document");
    } finally {
      setActionLoading(null);
    }
  }

  async function handleVerification(status: "PENDING" | "IN_REVIEW" | "APPROVED" | "REJECTED") {
    setActionLoading(`verify-${status}`);
    setActionError(null);
    try {
      await updateDriverVerification(driverId, status);
      await loadDriver();
    } catch (err) {
      setActionError(err instanceof Error ? err.message : "Failed to update verification");
    } finally {
      setActionLoading(null);
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64 gap-3">
        <svg className="animate-spin h-5 w-5 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
        </svg>
        <span className="text-gray-500">Loading driver details...</span>
      </div>
    );
  }

  if (error || !driver) {
    return (
      <div className="flex flex-col items-center justify-center h-64 gap-4">
        <p className="text-red-500">{error || "Driver not found"}</p>
        <button
          onClick={() => router.push("/drivers")}
          className="text-indigo-600 hover:text-indigo-800 text-sm font-medium"
        >
          Back to Drivers
        </button>
      </div>
    );
  }

  const allDocsApproved =
    driver.documents.length > 0 && driver.documents.every((d) => d.status === "APPROVED");

  return (
    <div>
      {/* Header */}
      <div className="flex items-center gap-4 mb-6">
        <button
          onClick={() => router.push("/drivers")}
          className="p-2 rounded-lg hover:bg-gray-200 transition-colors"
        >
          <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">
            {driver.name || <span className="text-gray-400 italic">No name</span>}
          </h1>
          <p className="text-sm text-gray-500">{driver.email}</p>
        </div>
        <span
          className={`ml-auto px-3 py-1 text-sm font-medium rounded-full border ${
            VERIFICATION_STYLES[driver.verificationStatus] || "bg-gray-100"
          }`}
        >
          {VERIFICATION_LABELS[driver.verificationStatus] || driver.verificationStatus}
        </span>
      </div>

      {/* Action error banner */}
      {actionError && (
        <div className="mb-4 flex items-center justify-between bg-red-50 border border-red-200 rounded-lg px-4 py-3">
          <p className="text-sm text-red-700">{actionError}</p>
          <button
            onClick={() => setActionError(null)}
            className="text-red-400 hover:text-red-600 text-sm font-medium ml-4"
          >
            Dismiss
          </button>
        </div>
      )}

      {/* Driver Info Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <InfoCard label="Phone" value={driver.phone || "Not provided"} />
        <InfoCard label="Emergency Contact" value={driver.emergencyContact || "Not provided"} />
        <InfoCard label="Joined" value={new Date(driver.createdAt).toLocaleDateString()} />
        <InfoCard label="Rating" value={driver.rating > 0 ? driver.rating.toFixed(1) : "No ratings"} />
        <InfoCard label="Total Trips" value={String(driver.totalTrips)} />
        <InfoCard
          label="Online Status"
          value={driver.isOnline ? "Online" : "Offline"}
          valueClassName={driver.isOnline ? "text-green-600" : "text-gray-500"}
        />
      </div>

      {/* Vehicle Info */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-5 mb-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-3">Vehicle Details</h2>
        {driver.vehicle ? (
          <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
            <Detail label="Type" value={driver.vehicle.type} />
            <Detail label="Make" value={driver.vehicle.make} />
            <Detail label="Model" value={driver.vehicle.model} />
            <Detail label="Year" value={String(driver.vehicle.year)} />
            <Detail label="License Plate" value={driver.vehicle.licensePlate} />
            <Detail label="Color" value={driver.vehicle.color} />
          </div>
        ) : (
          <p className="text-gray-400 text-sm">No vehicle information submitted</p>
        )}
      </div>

      {/* Documents Section */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-5 mb-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Uploaded Documents</h2>

        {driver.documents.length === 0 ? (
          <p className="text-gray-400 text-sm">No documents uploaded yet</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
            {driver.documents.map((doc) => (
              <div
                key={doc.id}
                className="border border-gray-200 rounded-lg overflow-hidden"
              >
                {/* Document image */}
                <div
                  className="relative h-48 bg-gray-100 cursor-pointer group"
                  onClick={() => setPreviewImage(doc.imageUrl)}
                >
                  {/* eslint-disable-next-line @next/next/no-img-element */}
                  <img
                    src={doc.imageUrl}
                    alt={DOC_TYPE_LABELS[doc.type] || doc.type}
                    className="w-full h-full object-cover"
                  />
                  <div className="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-colors flex items-center justify-center">
                    <span className="text-white opacity-0 group-hover:opacity-100 transition-opacity text-sm font-medium">
                      Click to enlarge
                    </span>
                  </div>
                </div>

                {/* Document info + actions */}
                <div className="p-3">
                  <div className="flex items-center justify-between mb-2">
                    <p className="text-sm font-medium text-gray-900">
                      {DOC_TYPE_LABELS[doc.type] || doc.type}
                    </p>
                    <span
                      className={`px-2 py-0.5 text-xs font-medium rounded-full ${
                        DOC_STATUS_STYLES[doc.status] || "bg-gray-100"
                      }`}
                    >
                      {doc.status}
                    </span>
                  </div>

                  <p className="text-xs text-gray-400 mb-3">
                    Uploaded {new Date(doc.uploadedAt).toLocaleDateString()}
                  </p>

                  {doc.expiryDate && (
                    <p className="text-xs text-gray-500 mb-3">
                      Expires {new Date(doc.expiryDate).toLocaleDateString()}
                    </p>
                  )}

                  {doc.rejectionReason && (
                    <p className="text-xs text-red-600 bg-red-50 rounded p-2 mb-3">
                      Rejection reason: {doc.rejectionReason}
                    </p>
                  )}

                  {/* Action buttons — only show for PENDING docs */}
                  {doc.status === "PENDING" && (
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleApproveDoc(doc)}
                        disabled={actionLoading === doc.id}
                        className="flex-1 px-3 py-1.5 text-xs font-medium rounded-lg bg-green-600 text-white hover:bg-green-700 disabled:opacity-50 transition-colors"
                      >
                        {actionLoading === doc.id ? "..." : "Approve"}
                      </button>
                      <button
                        onClick={() => {
                          setRejectDocId(doc.id);
                          setRejectionReason("");
                        }}
                        disabled={actionLoading === doc.id}
                        className="flex-1 px-3 py-1.5 text-xs font-medium rounded-lg bg-red-600 text-white hover:bg-red-700 disabled:opacity-50 transition-colors"
                      >
                        Reject
                      </button>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Verification Actions */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-5 mb-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-2">Driver Verification</h2>
        <p className="text-sm text-gray-500 mb-4">
          Update the driver&apos;s overall verification status. Approving allows the driver to start accepting deliveries.
        </p>

        {allDocsApproved && driver.verificationStatus !== "APPROVED" && (
          <div className="bg-green-50 border border-green-200 rounded-lg p-3 mb-4">
            <p className="text-sm text-green-800">
              All documents have been approved. This driver is ready to be verified.
            </p>
          </div>
        )}

        <div className="flex flex-wrap gap-3">
          {driver.verificationStatus !== "APPROVED" && (
            <button
              onClick={() => handleVerification("APPROVED")}
              disabled={actionLoading?.startsWith("verify")}
              className="px-4 py-2 text-sm font-medium rounded-lg bg-green-600 text-white hover:bg-green-700 disabled:opacity-50 transition-colors"
            >
              {actionLoading === "verify-APPROVED" ? "Approving..." : "Approve Driver"}
            </button>
          )}
          {driver.verificationStatus !== "IN_REVIEW" && driver.verificationStatus !== "APPROVED" && (
            <button
              onClick={() => handleVerification("IN_REVIEW")}
              disabled={actionLoading?.startsWith("verify")}
              className="px-4 py-2 text-sm font-medium rounded-lg bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50 transition-colors"
            >
              {actionLoading === "verify-IN_REVIEW" ? "..." : "Mark In Review"}
            </button>
          )}
          {driver.verificationStatus !== "REJECTED" && driver.verificationStatus !== "PENDING" && (
            <button
              onClick={() => handleVerification("REJECTED")}
              disabled={actionLoading?.startsWith("verify")}
              className="px-4 py-2 text-sm font-medium rounded-lg bg-red-600 text-white hover:bg-red-700 disabled:opacity-50 transition-colors"
            >
              {actionLoading === "verify-REJECTED" ? "..." : "Reject Driver"}
            </button>
          )}
          {driver.verificationStatus === "APPROVED" && (
            <p className="flex items-center gap-2 text-green-700 text-sm font-medium">
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              Driver is verified and can accept deliveries
            </p>
          )}
        </div>
      </div>

      {/* Rejection Reason Modal */}
      {rejectDocId && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl shadow-lg p-6 w-full max-w-md mx-4">
            <h3 className="text-lg font-semibold text-gray-900 mb-2">Reject Document</h3>
            <p className="text-sm text-gray-500 mb-4">
              Please provide a reason for rejection. The driver will see this and can re-upload.
            </p>
            <textarea
              value={rejectionReason}
              onChange={(e) => setRejectionReason(e.target.value)}
              placeholder="e.g., Image is blurry, document is expired..."
              className="w-full border border-gray-300 rounded-lg p-3 text-sm text-gray-900 placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 resize-none"
              rows={3}
              autoFocus
            />
            <div className="flex justify-end gap-3 mt-4">
              <button
                onClick={() => {
                  setRejectDocId(null);
                  setRejectionReason("");
                }}
                className="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleRejectDoc}
                disabled={!rejectionReason.trim() || actionLoading === rejectDocId}
                className="px-4 py-2 text-sm font-medium rounded-lg bg-red-600 text-white hover:bg-red-700 disabled:opacity-50 transition-colors"
              >
                {actionLoading === rejectDocId ? "Rejecting..." : "Reject Document"}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Image Preview Modal */}
      {previewImage && (
        <div
          className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 cursor-pointer"
          onClick={() => setPreviewImage(null)}
        >
          <div className="relative max-w-4xl max-h-[90vh] mx-4">
            <button
              onClick={() => setPreviewImage(null)}
              className="absolute -top-10 right-0 text-white hover:text-gray-300 text-sm font-medium"
            >
              Close (ESC)
            </button>
            {/* eslint-disable-next-line @next/next/no-img-element */}
            <img
              src={previewImage}
              alt="Document preview"
              className="max-w-full max-h-[85vh] rounded-lg object-contain"
              onClick={(e) => e.stopPropagation()}
            />
          </div>
        </div>
      )}
    </div>
  );
}

function InfoCard({
  label,
  value,
  valueClassName,
}: {
  label: string;
  value: string;
  valueClassName?: string;
}) {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
      <p className="text-xs text-gray-500 uppercase tracking-wider mb-1">{label}</p>
      <p className={`text-sm font-medium ${valueClassName || "text-gray-900"}`}>{value}</p>
    </div>
  );
}

function Detail({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <p className="text-xs text-gray-500">{label}</p>
      <p className="text-sm font-medium text-gray-900">{value || "-"}</p>
    </div>
  );
}
